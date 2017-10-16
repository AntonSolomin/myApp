package com.myApp.myApp;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import org.cloudinary.json.JSONObject;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/**
 * Created by Anton on 13-Oct-17.
 */
public class ImagesReciever {
    public List<String> urls = new ArrayList<>();


    public ImagesReciever(List<MultipartFile> files) {
        urls = files.stream().map(file -> getImageUrl(file)).collect(toList());
    }

    public String getImageUrl (MultipartFile file) {
        Cloudinary cloudinary = new Cloudinary("cloudinary://535928336455433:EEgAQDFCI0i-Fe86KvrgsQlBvBI@dq4elvg0g");
        String string = "";
        if (file.isEmpty()){
            return string;
        }
        try {
            File f= Files.createTempFile("temp", file.getOriginalFilename()).toFile();
            file.transferTo(f);
            Map response = cloudinary.uploader().upload(f, ObjectUtils.emptyMap());
            JSONObject json=new JSONObject(response);

            String urlCropped = cloudinary.url().format("jpg")
                    .transformation(new Transformation().width(250).height(168).crop("fit"))
                    .generate();

            return json.getString("url");

        } catch (IOException e){
            e.printStackTrace();
        }
        return string;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }
}
