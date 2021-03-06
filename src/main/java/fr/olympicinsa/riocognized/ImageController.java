/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.olympicinsa.riocognized;

import fr.olympicinsa.riocognized.exception.MyExceptionHandler;
import fr.olympicinsa.riocognized.model.Image;
import fr.olympicinsa.riocognized.model.ImageFace;
import fr.olympicinsa.riocognized.repository.ImageRepository;
import fr.olympicinsa.riocognized.service.ImageService;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author alex
 */

@Controller
@RequestMapping("/image")
@ComponentScan("fr.olympicinsa.riocognized.repository")
public class ImageController extends MyExceptionHandler {

    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private ImageService imageService;

    @RequestMapping("")
    public String index(@RequestParam(value="page", defaultValue = "1") Integer pageNumber, Map<String, Object> map) {
        Page<Image> page = imageService.getImageFace(pageNumber);
        int current = page.getNumber() + 1;
        int begin = Math.max(1, current - 5);
        int end = Math.min(begin + 10, page.getTotalPages());
        try {
            map.put("beginIndex", begin);
            map.put("endIndex", end);
            map.put("currentIndex", current);
            map.put("image", new Image());
            map.put("imageList", page);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "image";
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String save(
        @ModelAttribute("image") Image image,
        @RequestParam("file") MultipartFile file) {

        System.out.println("Name:" + image.getName());
        System.out.println("Desc:" + image.getDescription());
        System.out.println("File:" + file.getName());
        System.out.println("ContentType:" + file.getContentType());

        try {
            byte[] blob = IOUtils.toByteArray(file.getInputStream());

            image.setFilename(file.getOriginalFilename());
            image.setContent(blob);
            image.setContentType(file.getContentType());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            imageRepository.save(image);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/image";
    }

    @RequestMapping("/download/{imageId}")
    public String download(@PathVariable("imageId") Long imageId, HttpServletResponse response) {

        Image doc = imageRepository.findOne(imageId);
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(doc.getContent());
            response.setHeader("Content-Disposition", "inline;filename=\"" + doc.getFilename() + "\"");
            OutputStream out = response.getOutputStream();
            response.setContentType(doc.getContentType());
            IOUtils.copy(bis, out);
            out.flush();
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping("/remove/{imageId}")
    public String remove(@PathVariable("imageId") Long imageId) {

        imageRepository.delete(imageId);

        return "redirect:/image";
    }

    /* API POST Method*/
    @RequestMapping(value = "/api/upload", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public Image handleFileUpload(@RequestBody final Image image) {
        if (image.getContent().length < 1 || !image.getContentType().startsWith("image")) {
            throw new InvalidContent();
        }
        Image created = imageRepository.save(image);
        return created;
    }

}
