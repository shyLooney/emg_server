package com.server.controller.api;

import com.server.filter.DefaultFilter;
import com.server.filter.Filter;
import com.server.model.DefaultNeuralNetwork;
import com.server.model.NeuralNetwork;
import com.server.storage.FileSystemStorageService;
import com.server.storage.StorageProperties;
import com.server.storage.StorageService;
import net.lingala.zip4j.ZipFile;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/upload")
public class UploadRestController {
    private final StorageService storageService;
    private final List<Filter> filterList;
    private final List<NeuralNetwork> neuralNetworkList;

    public UploadRestController(StorageService storageService,
                                List<Filter> filterList,
                                List<NeuralNetwork> neuralNetworkList) {
        this.storageService = storageService;
        this.filterList = filterList;
        this.neuralNetworkList = neuralNetworkList;
        storageService.init();
//        StorageProperties storageProperties = new StorageProperties();
//        storageProperties.setLocation(modelsPath);
//        storageService = new FileSystemStorageService(storageProperties);
//        storageService.init();
    }

    @PostMapping("/model")
    public void uploadModel(@RequestParam("file") List<MultipartFile> files,
                            RedirectAttributes redirectAttributes) {
        String dir = "models";
        for (var item : files) {
            if (item.getContentType().equals("application/x-zip-compressed")) {
                storageService.store(item, dir);

                try (ZipFile zip = new ZipFile(storageService
                        .getLocation()
                        .resolve(dir)
                        .resolve(Objects.requireNonNull(item.getOriginalFilename()))
                        .toFile());) {


                    zip.extractAll(storageService.getLocation().resolve(dir).toString());
                    zip.getFile().delete();

                    neuralNetworkList.add(new DefaultNeuralNetwork(storageService
                            .getLocation()
                            .resolve("models")
                            .resolve(zip.getFileHeaders().get(0).toString())));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @PostMapping("/filter")
    public void uploadFilter(@RequestParam("file") List<MultipartFile> files,
                             RedirectAttributes redirectAttributes) {
        String dir = "filters";

        for (var item : files) {
            System.out.println(item.getContentType());
            if (item.getContentType().equals("text/plain")) {
                storageService.store(item, dir);
                System.out.println(item.getOriginalFilename());
                filterList.add(new DefaultFilter(storageService
                        .getLocation()
                        .resolve("filters")
                        .resolve(item.getOriginalFilename())));
            }
        }
    }

    @GetMapping("/filter")
    public List<String> getListFilter() {
        List<String> list = new ArrayList<>();

        for (var item : filterList) {
            list.add(item.getName());
        }
        return list;
    }

    @GetMapping("/model")
    public List<String> getListModel() {
        List<String> list = new ArrayList<>();

        for (var item : neuralNetworkList) {
            list.add(item.getName());
        }

        return list;
    }


}
