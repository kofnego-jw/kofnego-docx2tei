package playground.testserver.web;

import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Joseph on 29.10.2015.
 */
@Controller
@RequestMapping("/uploads")
public class UploadController {

    @ResponseBody
    @RequestMapping(value = "/upload")
    public NameAndContentResponse upload(@RequestParam("file") MultipartFile file, @RequestParam("username") String username) throws IOException {

        InputStream bytes;

        if (!file.isEmpty()) {
            bytes = file.getInputStream();
            FileUtils.copyInputStreamToFile(bytes, new File("tmp/" + file.getOriginalFilename()));
        }

        String response = String.format("receive ÄÖÜ %s from %s", file.getOriginalFilename(), username);
        System.out.println(response);
//        byte[] content = response.getBytes("utf-8");

        return new NameAndContentResponse(file.getOriginalFilename()+".xml", response.getBytes("utf-8"));

    }

}
