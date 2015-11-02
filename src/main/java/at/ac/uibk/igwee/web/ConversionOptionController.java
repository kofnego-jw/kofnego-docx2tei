package at.ac.uibk.igwee.web;

import at.ac.uibk.igwee.controller.ProgramSetup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Joseph on 31.10.15.
 */
@Controller
public class ConversionOptionController {

    @Autowired
    private ProgramSetup programSetup;

    @RequestMapping("/options")
    @ResponseBody
    public List<ConversionOptionFW> getOptions() {

        if (programSetup==null) throw new RuntimeException("Cannot read the program setup.");

        return programSetup.getConversionOptions()
                .stream()
                .map(ConversionOptionFW::create)
                .filter(x -> x!=null)
                .collect(Collectors.toList());


    }

}
