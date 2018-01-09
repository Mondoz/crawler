package com.hiekn.scraj.uyint.common.html.visitor;

import org.htmlparser.Parser;
import org.htmlparser.Remark;
import org.htmlparser.Tag;
import org.htmlparser.Text;
import org.htmlparser.visitors.NodeVisitor;

/**
 * @author xiaohuqi E-mail:xiaohuqi@126.com
 * @version 创建时间：2010-5-27 下午11:55:39
 * DIV访问器，主要用于得到页面内容时使用
 */
public class DivVisitor extends NodeVisitor {
	private String divText = "";
	private int tagCount = 0;
	
	/**
	 * 访问注释结点，其实注释已在前面过滤过了，此步骤多余
	 */
	public void visitRemarkNode(Remark remark) {}
	
	/**
	 * 
	 */
	public void visitStringNode(Text text) {
		String textValue = text.getText().trim();
		
		if(!textValue.equals("")){
//			Tag parentTag = (Tag)text.getParent();
//			//父结点为段落结点
//			if(parentTag!=null && parentTag.getTagName()!=null && parentTag.getTagName().equalsIgnoreCase("p")){
//				textValue = "<p> " + textValue + " </p> ";
//			}
			divText = divText.concat(textValue);
		}
		
	}

	public void visitTag(Tag tag) {
		String tagName = tag.getTagName();
		++ tagCount;
		//为段落标签和换行标签，此两种类型的标签在正式内容中一般用得较多
		if(tagName.equalsIgnoreCase("BR") || tagName.equalsIgnoreCase("P")){
			tagCount -= 5;
			divText += " <br /> ";
		}
		else if(tagName.equalsIgnoreCase("A")){
			-- tagCount;
		}
	}

	public static void main(String[] args) {
		try{
			Parser parser = new Parser ();
			parser.setInputHTML("");
			parser.setURL("h:/c/23d67725-1aa6-4c16-8f53-44a2c00630b6.htm");
			DivVisitor visitor = new DivVisitor();
	        parser.visitAllNodesWith (visitor);
	        parser.setURL("h:/c/25be76ab-256b-4be5-820f-e5e03586ca23.htm");
	        parser.visitAllNodesWith (visitor);
	        
	        
//	        File targetFile = new File("h:/swap/23dec53d-4cdb-4631-8dc9-c27697205f41.htm");
//	        FileOutputStream fos = new FileOutputStream(targetFile);
//			OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
//			String fileSrc =  + "\n" + visitor.keyWords + "\n" + visitor.description + "\n" + visitor.hyperlinkContent + "\n" + 
//					visitor.emphasizeContent + "\n" + visitor.nomalContent;
//			osw.write(fileSrc);
//			osw.flush();
//			osw.close();
		}catch(Exception e){                                                                                                                                                                                                                                                         
			e.printStackTrace();
		}
	}

	public String getDivText() {
		return divText;
	}

	public void setDivText(String divText) {
		this.divText = divText;
	}

	public int getTagCount() {
		return tagCount;
	}

	public void setTagCount(int tagCount) {
		this.tagCount = tagCount;
	}
}

