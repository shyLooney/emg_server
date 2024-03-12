package com.server.controller.api;

import com.server.storage.FileSystemStorageService;
import com.server.storage.StorageProperties;
import com.server.storage.StorageService;
import net.lingala.zip4j.ZipFile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/upload/model")
public class UploadRestController {
    private final StorageService storageService;
    private final String modelsPath = "upload/models";

    public UploadRestController() {
        StorageProperties storageProperties = new StorageProperties();
        storageProperties.setLocation(modelsPath);
        storageService = new FileSystemStorageService(storageProperties);
        storageService.init();
    }

    @PostMapping
    public void upload(@RequestParam("file") List<MultipartFile> files,
                       RedirectAttributes redirectAttributes) {
        for (var item : files) {
            if (item.getContentType().equals("application/x-zip-compressed")) {
                storageService.store(item);

                try (ZipFile zip = new ZipFile(modelsPath + "/" + item.getOriginalFilename());) {
                    zip.extractAll(modelsPath);
                    zip.getFile().delete();

//            redirectAttributes.addFlashAttribute("message",
//                    "You successfully uploaded " + item.getOriginalFilename() + "!");
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }
    }
}
