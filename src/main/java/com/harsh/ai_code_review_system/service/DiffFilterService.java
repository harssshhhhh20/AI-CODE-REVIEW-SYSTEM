package com.harsh.ai_code_review_system.service;


import org.springframework.stereotype.Service;

@Service
public class DiffFilterService {
    public String filterDiff(String diff){
        StringBuilder filtered = new StringBuilder();
        String[] lines = diff.split("\n");
        boolean skipFile = false;
        for (String line : lines){
            if(line.startsWith("diff --git")){
                skipFile =
                        line.contains(".DS_Store")
                                || line.contains(".pkl")
                                || line.contains(".png")
                                || line.contains(".jpg")
                                || line.contains(".jpeg")
                                || line.contains(".gif")
                                || line.contains(".pdf")
                                || line.contains(".zip")
                                || line.contains(".jar")
                                || line.contains(".class");
                if(!skipFile)
                    filtered.append(line).append("\n");
                continue;
            }
            if(skipFile)
                continue;
            if(line.contains("Binary files"))
                continue;
            filtered.append(line).append("\n");
        }
        return filtered.toString();
    }
}
