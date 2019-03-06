package model;

import java.io.Serializable;

public class FileNode implements Serializable{
	private String uuid;
	private String name;
	private long size;
	private String mainServerIp;
	private Integer mainServerNode;
	private String copyServerIp;
	private Integer copyServerNode;
	private static final long serialVersionUID=7981560250804078637l; 
	public FileNode() {
		super();
		// TODO Auto-generated constructor stub
	}
	@Override
	public String toString() {
		return "FileNode [uuid=" + uuid + ", name=" + name + ", size=" + size + ", mainServerIp=" + mainServerIp
				+ ", mainServerNode=" + mainServerNode + ", copyServerIp=" + copyServerIp + ", copyServerNode="
				+ copyServerNode + "]";
	}
	public FileNode(String uuid, String name,long size, String mainServerIp, Integer mainServerNode,
			String copyServerIp, Integer copyServerNode) {
		super();
		this.uuid = uuid;
		this.name = name;
		this.size = size;
		this.mainServerIp = mainServerIp;
		this.mainServerNode = mainServerNode;
		this.copyServerIp = copyServerIp;
		this.copyServerNode = copyServerNode;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public String getMainServerIp() {
		return mainServerIp;
	}
	public void setMainServerIp(String mainServerIp) {
		this.mainServerIp = mainServerIp;
	}
	public Integer getMainServerNode() {
		return mainServerNode;
	}
	public void setMainServerNode(Integer mainServerNode) {
		this.mainServerNode = mainServerNode;
	}
	public String getCopyServerIp() {
		return copyServerIp;
	}
	public void setCopyServerIp(String copyServerIp) {
		this.copyServerIp = copyServerIp;
	}
	public Integer getCopyServerNode() {
		return copyServerNode;
	}
	public void setCopyServerNode(Integer copyServerNode) {
		this.copyServerNode = copyServerNode;
	}
}
