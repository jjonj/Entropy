package jjj.entropy.ui;


import java.awt.Graphics;
import java.awt.Image;
import java.awt.TextField;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;

import javax.media.opengl.awt.GLCanvas;

import javax.imageio.ImageIO;

import jjj.entropy.Game;

/*
 * Add Set color
 * Add Set size / style / type (create new TextRenderer)
 */
public class EntTextfield extends EntUIComponent{
	private GLCanvas canvas;
	private String text;
    private BufferedImage img;
    private BufferedImage bi;
    private Graphics g;
    
    
    
	public EntTextfield(int x, int y, GLCanvas canvas) {
		super(x, y);
		text = "testtext";
		/*this.canvas = canvas;
		File i = new File("resources/images/textfield.png");
		try {
			img = ImageIO.read(i);
			int w = img.getWidth(null);
			int h = img.getHeight(null);
			bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			
			g = bi.getGraphics();

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		*/

	}
	
	public String GetText()
	{
		return text;
	}
	public void SetText(String text) 
	{
		this.text = text;
	}
	
	public void Render()
	{
		
		//Graphics gg = canvas.getGraphics();
	//	System.out.println("ERROR1");
	//	System.exit(1);
	//	if (gg == null)
			
	//	{
			
			
	//	}
/*		ImageObserver iob = new ImageObserver() {
			
			@Override
			public boolean imageUpdate(Image arg0, int arg1, int arg2, int arg3,
					int arg4, int arg5) {
				// TODO Auto-generated method stub
				return false;
			}
		};
		gg.drawImage(img, x, y, iob);*/
		
		//g.drawImage(img, x, y, null);

		/*


		// * Create a rescale filter op that makes the image
		 //* 50% opaque.

		float[] scales = { 1f, 1f, 1f, 0.5f };
		float[] offsets = new float[4];
		RescaleOp rop = new RescaleOp(scales, offsets, null);

		// Draw the image, applying the filter
		g2d.drawImage(bi, rop, 0, 0);*/
	}

	
}
