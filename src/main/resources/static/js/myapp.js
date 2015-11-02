var withoutPort = true;

var myApp = angular.module("myApp", ['ngSanitize', 'ngFileUpload', 'ui.bootstrap']);

myApp.service("conversionOptionsService", ["$http", "$rootScope",
    function($http, $rootScope) {
        var conversionOptions = [];

        this.getConversionOptions = function() {
            return conversionOptions;
        };

        this.loadConversionOptions = function() {
            console.log("Getting ConversionOptions...");
            $http({
                method: 'GET',
                url   : withoutPort ? 'options' : 'http://localhost:8080/options'
            })
                .then(function(resp){
                    conversionOptions = resp.data;
                    $rootScope.$broadcast("ConversionOptionsAvailable");
                }, function(resp){
                    alert("Cannot load conversion options.");
                });
        };

        this.loadConversionOptions();

    }]);

myApp.controller("routeController", ["$scope",
    function ($scope) {
        $scope.template = "templates/start.html";
        $scope.showImpressum = function () {
            $scope.template = "templates/impressum.html";
        };
        $scope.showMain = function () {
            $scope.template = "templates/start.html";
        };
        $scope.showHelp = function () {
            $scope.template = "templates/help.html";
        }
    }]);

myApp.controller("testUploadController", ['$scope', 'Upload', 'conversionOptionsService',
    function ($scope, Upload, conversionOptionsService) {

        $scope.conversionOptions = [];

        $scope.selectedConversionOptions = [];

        $scope.$on("ConversionOptionsAvailable", function() {
            $scope.conversionOptions = conversionOptionsService.getConversionOptions();
            console.log($scope.conversionOptions);
        });

        this.addConversionOption = function(coName) {
            var co;
            for (var i=0; i<$scope.conversionOptions.length; i++) {
                if ($scope.conversionOptions[i].name === coName) {
                    co = $scope.conversionOptions[i];
                    break;
                }
            }
            if (co!==null)
                $scope.selectedConversionOptions.push(co);
        };

        this.removeConversionOption = function(index) {
            $scope.selectedConversionOptions.splice(index, 1);
        };

        // upload later on form submit or something similar
        $scope.submitAndDownload = function() {
            upload($scope.file, true);
        };

        $scope.submitAndView = function() {
            upload($scope.file, false);
        };

        // upload on file select or drop
        var upload = function (file, download) {
            var cos = [];
            $scope.selectedConversionOptions.forEach(function(co) {
                cos.push(co.name);
            });

            Upload.upload({
                url: withoutPort ? 'convert' : 'http://localhost:8080/convert',
                data: {
                    file: file,
                    conversionOptions: JSON.stringify(cos)
                },
                responseType: "arraybuffer"
            }).then(function (resp) {
                var data = resp.data;
                console.log('Success ' + resp.config.data.file.name + 'uploaded. Response: ' + resp.status);

                var blob = new Blob([data], {type: "text/xml;charset=UTF-8"});

                if (download) {
                    var filename = resp.config.data.file.name + ".xml";
                    saveAs(blob, filename);
                } else {
                    var url = URL.createObjectURL(blob);
                    window.open(url, "_blank");
                }

            }, function (resp) {
                alert("Cannot convert: " + resp.status);
            }, function (evt) {
                var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                console.log('progress: ' + progressPercentage + '% ' + evt.config.data.file.name);
            });
        };

    }]);
