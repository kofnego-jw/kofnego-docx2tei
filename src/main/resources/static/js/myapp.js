var withoutPort = false;

var myApp = angular.module("myApp", ['ngSanitize', 'ngFileUpload', 'ui.bootstrap']);

myApp.service("loadingNowService", ['$rootScope',
    function($rootScope){
        var loadingNow = false;
        this.setLoadingNow = function(bool) {
            loadingNow = bool;
            $rootScope.$broadcast("LoadingNowChanged");
        };
        this.getLoadingNow = function(){
            return loadingNow;
        };
    }]);

myApp.service("conversionOptionsService", ["$http", "$rootScope",
    function($http, $rootScope) {
        var conversionOptions = [];

        this.getConversionOptions = function() {
            return conversionOptions;
        };

        this.loadConversionOptions = function() {
            //console.log("Getting ConversionOptions...");
            $http({
                method: 'GET',
                url   : withoutPort ? 'options' : 'http://localhost:1340/options'
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

myApp.controller("routeController", ["$scope", "loadingNowService",
    function ($scope, loadingNowService) {
        $scope.template = "templates/start.html";
        $scope.loadingNow = false;
        $scope.$on("LoadingNowChanged", function(){
            $scope.loadingNow = loadingNowService.getLoadingNow();
        });
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

myApp.controller("testUploadController", ['$scope', 'Upload', 'conversionOptionsService', 'loadingNowService',
    function ($scope, Upload, conversionOptionsService, loadingNowService) {

        $scope.conversionOptions = [];

        $scope.selectedConversionOptions = [];

        loadingNowService.setLoadingNow(true);

        $scope.$on("ConversionOptionsAvailable", function() {
            $scope.conversionOptions = conversionOptionsService.getConversionOptions();
            //console.log($scope.conversionOptions);
            loadingNowService.setLoadingNow(false);
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

            loadingNowService.setLoadingNow(true);

            Upload.upload({
                url: withoutPort ? 'convert' : 'http://localhost:1340/convert',
                data: {
                    file: file,
                    conversionOptions: JSON.stringify(cos)
                },
                responseType: "arraybuffer"
            }).then(function (resp) {
                var data = resp.data;
                //console.log('Success ' + resp.config.data.file.name + 'uploaded. Response: ' + resp.status);
                loadingNowService.setLoadingNow(false);

                var blob = new Blob([data], {type: "text/xml;charset=UTF-8"});

                if (download) {
                    var filename = resp.config.data.file.name + ".xml";
                    saveAs(blob, filename);
                } else {
                    var url = URL.createObjectURL(blob);
                    window.open(url, "_blank");
                }

            }, function (resp) {
                loadingNowService.setLoadingNow(false);

                var data = JSON.parse(String.fromCharCode.apply(null, new Uint8Array(resp.data)));

                alert("Cannot convert: " + data.status + " " + data.message);

            }, function (evt) {
                // loadingNowService.setLoadingNow(false);
            });
        };

    }]);
