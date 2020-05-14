package mhel.itu.bachelor.shortestpathmap.api.dto;

import java.io.Serializable;

public class ModelInfoDTO implements Serializable {
    public String modelName;
    public String fileName;
    public long fileSize;
    public int V;
    public int E;
    public int landmarks;
    public String fileOrigin;
}
