package models;

public class Airport {
	private String name;
	private String code;
	private int x,y;
	
	public Airport(String name,String code,int x,int y) {
		this.name=name;
		this.code=code;
		this.x=x;
		this.y=y;
	}
	
	public String getName() {return name;}
	public String getCode() {return code;}
	public int getX() {return x;}
	public int getY() {return y;}
	
	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder();
		sb.append(code).append(" -> ").append(name).
		append(" (").append(x).append(", ").append(y).append(")");
		return sb.toString();
	}
	
}
