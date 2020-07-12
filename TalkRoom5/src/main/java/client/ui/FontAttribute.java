package client.ui;

import lombok.Data;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
@Data
public class FontAttribute {
    private SimpleAttributeSet attributeSet;

    /*
     *字体设置
     * */
    private String name;
    private boolean bold;
    private boolean italic;
    private int size;
    private Color foreColor = Color.BLACK;
    private Color backColor = Color.WHITE;

    public FontAttribute(){

    }

    public FontAttribute(int type){
        switch(type){
            case 1:
                //时间字体
                this.setForeColor(Color.BLUE);
                this.setBold(false);
                this.setItalic(false);
                this.setSize(10);
                break;
            case 2:
                //“自己”字体
                this.setForeColor(Color.GREEN);
                this.setBold(true);
                this.setItalic(false);
                this.setSize(14);
                break;
            case 3:
                //“他人”字体
                this.setForeColor(Color.PINK);
                this.setBold(true);
                this.setItalic(false);
                this.setSize(14);
                break;
            case 4:
                //历史记录字体
                this.setForeColor(Color.GRAY);
                this.setBold(true);
                this.setItalic(true);
                this.setSize(14);
                break;
            case 5:
                //消息字体
                this.setForeColor(Color.black);
                this.setBold(true);
                this.setItalic(false);
                this.setSize(14);
        }
    }

    public SimpleAttributeSet getAttributeSet(){
        attributeSet = new SimpleAttributeSet();
        if(name != null){
            StyleConstants.setFontFamily(attributeSet,name);
        }
        StyleConstants.setBold(attributeSet,this.bold);
        StyleConstants.setItalic(attributeSet,this.italic);
        StyleConstants.setFontSize(attributeSet,this.size);
        if(foreColor != null){
            StyleConstants.setForeground(attributeSet,foreColor);
        }
        if(backColor != null){
            StyleConstants.setBackground(attributeSet,backColor);
        }
        return attributeSet;
    }
}
