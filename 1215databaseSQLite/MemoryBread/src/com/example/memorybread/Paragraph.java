package com.example.memorybread;

//ʵ����
public class Paragraph {
	Integer p_id;      //����id������
	String p_name;     //��������
	String p_content;  //��������
	Integer p_category;//���������һ�����͵�������ʾ���
	
	Paragraph() {
		
	}
	
	Paragraph(String _p_name, String _p_content, Integer _p_category) {
		p_name = _p_name;
		p_content = _p_content;
		p_category = _p_category;
	}
	
	Paragraph(Integer _p_id, String _p_name, String _p_content, Integer _p_category) {
		p_id = _p_id;
		p_name = _p_name;
		p_content = _p_content;
		p_category = _p_category;
	}
	
	public void setId(Integer _id) {
		p_id = _id;
	}
	
	public void setPname(String _p_name) {
		p_name = _p_name;
	}
	
	public void setPcontent(String _p_content) {
		p_content = _p_content;
	}
	
	public void setPcategory(Integer _p_category) {
		p_category = _p_category;
	}
	
	public Integer getId() {
		return p_id;
	}
	
	public String getPname() {
		return p_name;
	}
	
	public String getPcontent() {
		return p_content;
	}
	
	public Integer getPcategory() {
		return p_category;
	}
}
