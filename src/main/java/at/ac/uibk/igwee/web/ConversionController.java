package at.ac.uibk.igwee.web;

import at.ac.uibk.igwee.controller.MainConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

/**
 * Created by Joseph on 31.10.15.
 *
 * Main Controller for doing conversion.
 *
 * @author Joseph
 */
@Controller
public class ConversionController {

    /**
     * Object Mapper, used for converting conversion options to java object
     */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * MainConverter
     */
    @Autowired
    private MainConverter mainConverter;

    /**
     * Old method for conversion. Returns a JSON instead of ByteArray.
     * @param file the Multipart File to be converted
     * @param conversionOptions the conversion options. Should be a JSON String
     * @return a NameAndContentResponse
     * @throws Exception if anything goes wrong.
     */
//    @RequestMapping(value = "/convert", method = RequestMethod.POST)
//    @ResponseBody
    public NameAndContentResponse convert(@RequestParam("file")
                                              MultipartFile file,
                                          @RequestParam(value = "conversionOptions", required = false)
                                          String conversionOptions)
            throws Exception {

        List<String> cos = conversionOptions==null ? Collections.emptyList() :
                OBJECT_MAPPER.readValue(conversionOptions, List.class);

        InputStream converted = mainConverter.convert(file.getOriginalFilename(), file.getInputStream(), cos);
        String xml = IOUtils.toString(converted, "utf-8");
        String filename = file.getOriginalFilename().toLowerCase().endsWith(".docx") ?
                file.getOriginalFilename().substring(0, file.getOriginalFilename().length()-4) + "xml" :
                file.getOriginalFilename() + ".xml";
        return new NameAndContentResponse(filename, xml);
    }

    /**
     * The new method for conversion. Will return nothing, the result of conversion
     * is written into the OutputStream of the HttpServletResponse.
     * @param file the multipart file to be converted
     * @param conversionOptions a json string representing the conversion options
     * @param response the httpservletResponse, should be injected by Spring
     * @throws Exception
     */
    @RequestMapping(value="/convert", method = RequestMethod.POST)
    public void convert(@RequestParam("file") MultipartFile file,
                        @RequestParam(value = "conversionOptions", required = false) String conversionOptions,
                        HttpServletResponse response) throws Exception {
        List<String> cos = conversionOptions==null ? Collections.emptyList() :
                OBJECT_MAPPER.readValue(conversionOptions, List.class);

//        Thread.sleep(5000);

        InputStream converted = mainConverter.convert(file.getOriginalFilename(), file.getInputStream(), cos);
        response.setContentType("text/xml;charset=utf-8");
        OutputStream os = response.getOutputStream();
        IOUtils.copy(converted, os);
        if (os!=null) {
            try {
                os.close();
            } catch (Exception ignored) {

            }
        }
    }



}
