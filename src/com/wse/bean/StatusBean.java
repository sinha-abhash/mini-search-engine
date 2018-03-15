package com.wse.bean;

public class StatusBean {
	private int seedDocId;
	private int fromDocId;
	private int toDocId;
	private int depth;
	private boolean complete;
	
	public int getSeedDocId() {
		return seedDocId;
	}
	public void setSeedDocId(int seedDocId) {
		this.seedDocId = seedDocId;
	}
	public int getFromDocId() {
		return fromDocId;
	}
	public void setFromDocId(int fromDocId) {
		this.fromDocId = fromDocId;
	}
	public int getToDocId() {
		return toDocId;
	}
	public void setToDocId(int toDocId) {
		this.toDocId = toDocId;
	}
	public int getDepth() {
		return depth;
	}
	public void setDepth(int depth) {
		this.depth = depth;
	}
	public boolean isComplete() {
		return complete;
	}
	public void setComplete(boolean complete) {
		this.complete = complete;
	}
}
