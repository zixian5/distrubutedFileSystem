package model;

import java.io.Serializable;

public class FileStorage implements Serializable{
	private String name;
	private String IP;
	private Integer port;
	private Double maxVolume;
	private Double realVolume;
	private Double leftVolume;
	private Integer fileNum;
	private Boolean isAlive;
	private String storageDir;
	public FileStorage() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getStorageDir() {
		return storageDir;
	}
	public void setStorageDir(String storageDir) {
		this.storageDir = storageDir;
	}
	@Override
	public String toString() {
		return "FileStorage [name=" + name + ", IP=" + IP + ", port=" + port + ", maxVolume=" + maxVolume
				+ ", realVolume=" + realVolume + ", leftVolume=" + leftVolume + ", fileNum=" + fileNum + ", isAlive="
				+ isAlive + ", storageDir=" + storageDir + "]";
	}
	public FileStorage(String name, String iP, Integer port, Double maxVolume, Double realVolume, Double leftVolume,
			Integer fileNum, Boolean isAlive, String storageDir) {
		super();
		this.name = name;
		IP = iP;
		this.port = port;
		this.maxVolume = maxVolume;
		this.realVolume = realVolume;
		this.leftVolume = leftVolume;
		this.fileNum = fileNum;
		this.isAlive = isAlive;
		this.storageDir = storageDir;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIP() {
		return IP;
	}
	public void setIP(String iP) {
		IP = iP;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
	public Double getMaxVolume() {
		return maxVolume;
	}
	public void setMaxVolume(Double maxVolume) {
		this.maxVolume = maxVolume;
	}
	public Double getRealVolume() {
		return realVolume;
	}
	public void setRealVolume(Double realVolume) {
		this.realVolume = realVolume;
	}
	public Double getLeftVolume() {
		return leftVolume;
	}
	public void setLeftVolume(Double leftVolume) {
		this.leftVolume = leftVolume;
	}
	public Integer getFileNum() {
		return fileNum;
	}
	public void setFileNum(Integer fileNum) {
		this.fileNum = fileNum;
	}
	public Boolean getIsAlive() {
		return isAlive;
	}
	public void setIsAlive(Boolean isAlive) {
		this.isAlive = isAlive;
	}
	public FileStorage(String name, String iP, Integer port, Double maxVolume, Double realVolume, Double leftVolume,
			Integer fileNum, Boolean isAlive) {
		super();
		this.name = name;
		IP = iP;
		this.port = port;
		this.maxVolume = maxVolume;
		this.realVolume = realVolume;
		this.leftVolume = leftVolume;
		this.fileNum = fileNum;
		this.isAlive = isAlive;
	}
}
