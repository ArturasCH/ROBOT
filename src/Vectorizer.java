import com.googlecode.javacv.CanvasFrame;
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvSize;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2GRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BLUR;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_THRESH_BINARY;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_THRESH_OTSU;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCanny;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvSmooth;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvThreshold;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import net.coobird.thumbnailator.Thumbnails;
import net.iharder.dnd.FileDrop;

class Vectorizer {
/*
    prideti checkbox'a kad aktyvuot canny edge detection'a jei nores kas rasti black people
    */
    /**
     * Returns the supplied src image brightened by a float value from 0 to 10.
     * Float values below 1.0f actually darken the source image.
     */
    BufferedImage colorImage;
    BufferedImage bi = null;
    BufferedImage bw = null;
    BufferedImage result = null;
    File selected;
    
    public static BufferedImage brighten(BufferedImage src, float level) {
        BufferedImage dst = new BufferedImage(
                src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
        float[] scales = {level, level, level};
        float[] offsets = new float[4];
        RescaleOp rop = new RescaleOp(scales, offsets, null);

        Graphics2D g = dst.createGraphics();
        try {
            g.drawImage(src, rop, 0, 0);
        }
        catch(IllegalArgumentException e) {
            g.drawImage(src, 0, 0, null);
        }
        g.dispose();

        return dst;
    }
    
    public static BufferedImage detectEdges (BufferedImage image)
    {
        BufferedImage copy = Processer.copyImage(image);
        IplImage img = IplImage.createFrom(copy);
        IplImage imageGray = cvCreateImage(cvSize(img.width(), img.height()), IPL_DEPTH_8U, 1);
        cvSmooth( img, img, CV_BLUR, 3,3, 2, 2);
        IplImage output= cvCreateImage(cvSize(img.width(), img.height()), IPL_DEPTH_8U, 1);
        double otsu = cvThreshold(imageGray, imageGray, 0, 255, CV_THRESH_BINARY | CV_THRESH_OTSU );
        cvCvtColor(img, imageGray, CV_BGR2GRAY );
        IplImage gray = new IplImage(imageGray);
        IplImage edge = cvCreateImage(cvSize(imageGray.width(), imageGray.height()), IPL_DEPTH_8U, 1);
        cvCanny(imageGray, edge, otsu*0.4, otsu, 3);
        copy = edge.getBufferedImage();
        Processer.invert(copy);     
        
        return copy;
    }

    public void processer() throws Exception {
        //URL colorURL = new URL("http://i191.photobucket.com/albums/z227/jimmyavellaneda/davinci-mona-lisa-small.jpg");
        selected = new File("chooseImage.png");
        colorImage = ImageIO.read(getClass().getResource("chooseImage.png"));
        //colorImage = ImageIO.read(colorURL);

        float[] scales = {2f, 2f, 2f};
        float[] offsets = new float[4];
        RescaleOp rop = new RescaleOp(scales, offsets, null);

        final BufferedImage scaledImage = new BufferedImage(
                colorImage.getWidth(),
                colorImage.getHeight(),
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g = scaledImage.createGraphics();
        try {
            g.drawImage(colorImage, rop, 0, 0);
        }
        catch(IllegalArgumentException e) {
            g.drawImage(colorImage, 0, 0, null);
        }

        g.dispose();
        
        final JFrame frame = new JFrame();
        Runnable r = new Runnable() {

            @Override
            public void run() {
                         
                JPanel gui = new JPanel(new BorderLayout(2, 2));
                final JPanel images = new JPanel(new FlowLayout());
                gui.add(images, BorderLayout.CENTER);

                bi = brighten(colorImage, 0.950f);
                bw = new BufferedImage(
                    colorImage.getWidth(),
                    colorImage.getHeight(),
                    BufferedImage.TYPE_BYTE_BINARY);
                Graphics g = bw.createGraphics();
                g.drawImage(bi, 0, 0, null);
                g.dispose();
                final JLabel real = new JLabel(new ImageIcon(colorImage));
                real.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
                final JLabel scaled = new JLabel(new ImageIcon(bw));
                final JSlider brighten = new JSlider(500, 2000, 1250);
                brighten.setToolTipText("Set picture intensity");
                gui.add(brighten, BorderLayout.PAGE_START);
                
                ChangeListener cl = new ChangeListener() {

                    @Override
                    public void stateChanged(ChangeEvent e) {
                        int val = brighten.getValue();
                        float valFloat = val / 1000f;
                        bi = brighten(colorImage, valFloat);
                        bw = new BufferedImage(
                                colorImage.getWidth(),
                                colorImage.getHeight(),
                                BufferedImage.TYPE_BYTE_BINARY);
                        Graphics g = bw.createGraphics();
                        g.drawImage(bi, 0, 0, null);
                        g.dispose();

                        scaled.setIcon(new ImageIcon(bw));
                    }
                };
                brighten.addChangeListener(cl);

                JCheckBox edges = new JCheckBox("Canny Edge Detection", false);
                edges.addItemListener(new ItemListener(){

                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        if(e.getStateChange() == ItemEvent.SELECTED)
                        {
                            bi = brighten(colorImage, 1.25f);                             
                            brighten.removeChangeListener(cl);
                            bi = detectEdges(colorImage);                                       
                              bw = new BufferedImage(
                                    colorImage.getWidth(),
                                    colorImage.getHeight(),
                                    BufferedImage.TYPE_BYTE_BINARY);
                                Graphics g = bw.createGraphics();
                                g.drawImage(bi, 0, 0, null);
                                g.dispose();
                                images.removeAll();
                                
                                JPanel realP = new JPanel();
                                realP.setLayout(new BorderLayout());
                                realP.setBackground(Color.white);
                                realP.setPreferredSize(new Dimension(297, 420));
                                real.setIcon(new ImageIcon(colorImage));
                                realP.add(real);
                                
                                JPanel scaledP = new JPanel();
                                scaledP.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
                                scaledP.setLayout(new BorderLayout());
                                scaledP.setBackground(Color.white);
                                scaledP.setPreferredSize(new Dimension(297, 420));
                                scaled.setIcon(new ImageIcon(bw));
                                scaledP.add(scaled);
                                
                                images.add(realP);
                                images.add(scaledP);
                                //real.setIcon(new ImageIcon(colorImage));
                                //scaled.setIcon(new ImageIcon(bw));
                                
                                frame.revalidate();
                                frame.repaint();
                                brighten.setValue(1250);
                                      
                        }
                        else if(e.getStateChange() == ItemEvent.DESELECTED)
                        {
                            brighten.addChangeListener(cl);
                            bi = brighten(colorImage, 1.25f);
                            bw = new BufferedImage(
                                    colorImage.getWidth(),
                                    colorImage.getHeight(),
                                    BufferedImage.TYPE_BYTE_BINARY);
                                Graphics g = bw.createGraphics();
                                g.drawImage(bi, 0, 0, null);
                                g.dispose();
                                images.removeAll();
                                
                                JPanel realP = new JPanel();
                                realP.setLayout(new BorderLayout());
                                realP.setBackground(Color.white);
                                realP.setPreferredSize(new Dimension(297, 420));
                                real.setIcon(new ImageIcon(colorImage));
                                realP.add(real);
                                
                                JPanel scaledP = new JPanel();
                                scaledP.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
                                scaledP.setLayout(new BorderLayout());
                                scaledP.setBackground(Color.white);
                                scaledP.setPreferredSize(new Dimension(297, 420));
                                scaled.setIcon(new ImageIcon(bw));
                                scaledP.add(scaled);
                                
                                images.add(realP);
                                images.add(scaledP);
                                //real.setIcon(new ImageIcon(colorImage));
                                //scaled.setIcon(new ImageIcon(bw));
                                
                                frame.revalidate();
                                frame.repaint();
                                brighten.setValue(1250); 
                        }
                        
                        
                        
                        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }                    
                });
                
                
                
                
                
                
                JButton chooseFile = new JButton("Import image");
                //chooseFile.setPreferredSize(new Dimension(80, 20));
                chooseFile.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        UIManager.put("FileChooser.readOnly", Boolean.TRUE);
                        final JFileChooser fc = new JFileChooser();
                        fc.setDialogTitle("Choose Image");
                        fc.setFileFilter(new ImageFilter());
                        fc.setFileView(new ImageFileView());
                        fc.setAccessory(new ImagePreview(fc));
                        int returnValue = fc.showOpenDialog(null);
                        if (returnValue == JFileChooser.APPROVE_OPTION) {
                            selected = fc.getSelectedFile();
                            try {
                                colorImage = ImageIO.read(selected);
                                //colorImage.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                                if(colorImage.getWidth() > 297 || colorImage.getHeight() > 420)
                                    colorImage = Thumbnails.of(colorImage).size(297, 420).asBufferedImage();                     
                                bi = brighten(colorImage, 1.25f);
                                if(edges.isSelected())
                                {
                                 brighten.removeChangeListener(cl);
                                 bi = detectEdges(colorImage);                                       
                                }                                
                      //do something with change listener on brighten
                                bw = new BufferedImage(
                                    colorImage.getWidth(),
                                    colorImage.getHeight(),
                                    BufferedImage.TYPE_BYTE_BINARY);
                                Graphics g = bw.createGraphics();
                                g.drawImage(bi, 0, 0, null);
                                g.dispose();
                                images.removeAll();
                                
                                JPanel realP = new JPanel();
                                realP.setLayout(new BorderLayout());
                                realP.setBackground(Color.white);
                                realP.setPreferredSize(new Dimension(297, 420));
                                real.setIcon(new ImageIcon(colorImage));
                                realP.add(real);
                                
                                JPanel scaledP = new JPanel();
                                scaledP.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
                                scaledP.setLayout(new BorderLayout());
                                scaledP.setBackground(Color.white);
                                scaledP.setPreferredSize(new Dimension(297, 420));
                                scaled.setIcon(new ImageIcon(bw));
                                scaledP.add(scaled);
                                
                                images.add(realP);
                                images.add(scaledP);
                                //real.setIcon(new ImageIcon(colorImage));
                                //scaled.setIcon(new ImageIcon(bw));
                                
                                frame.revalidate();
                                frame.repaint();
                                brighten.setValue(1250);
                            } catch (IOException ex) {
                                Logger.getLogger(Vectorizer.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                } );  
                
                images.add(real);
                images.add(scaled);

                JButton ok = new JButton("OK");
                //ok.setPreferredSize(new Dimension(40, 20));
                
                JLabel original = new JLabel("Original picture");
                original.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 370));
                JLabel bin = new JLabel("Converted picture");
                JPanel labels = new JPanel();
                labels.setLayout(new FlowLayout());
                labels.add(original);
                labels.add(bin);
                
                JPanel buttons = new JPanel();
                buttons.setLayout(new FlowLayout());
                buttons.add(chooseFile);
                JLabel empty = new JLabel();
                empty.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 390));
                buttons.add(edges);
                buttons.add(empty);
                buttons.add(ok);
              
                JPanel LB = new JPanel();
                LB.setLayout(new BorderLayout());
                LB.add(labels, BorderLayout.PAGE_START);
                LB.add(buttons, BorderLayout.PAGE_END);
                
                new  FileDrop( frame, new FileDrop.Listener()
                {   public void  filesDropped( java.io.File[] files )
                    {
                        boolean b = false;
                        if(extension(files[0]).equalsIgnoreCase("GIF"))
                            b = true;
                        else if(extension(files[0]).equalsIgnoreCase("JPG"))
                            b = true;    
                        else if(extension(files[0]).equalsIgnoreCase("PNG"))
                            b = true;
                        else if(extension(files[0]).equalsIgnoreCase("JPEG"))
                            b = true;
                        else if(extension(files[0]).equalsIgnoreCase("TIF"))
                            b = true;
                        else if(extension(files[0]).equalsIgnoreCase("TIFF"))
                            b = true;
                        if(b) {
                            selected = files[0];
                            try {
                                colorImage = ImageIO.read(selected);
                                //colorImage.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                                if(colorImage.getWidth() > 297 || colorImage.getHeight() > 420)
                                    colorImage = Thumbnails.of(colorImage).size(297, 420).asBufferedImage();
                                bi = brighten(colorImage, 1.25f);
                                 if(edges.isSelected())
                                {
                                 brighten.removeChangeListener(cl);
                                 bi = detectEdges(colorImage);                                       
                                }                             
                                bw = new BufferedImage(
                                    colorImage.getWidth(),
                                    colorImage.getHeight(),
                                    BufferedImage.TYPE_BYTE_BINARY);
                                Graphics g = bw.createGraphics();
                                g.drawImage(bi, 0, 0, null);
                                g.dispose();
                                images.removeAll();
                                
                                JPanel realP = new JPanel();
                                realP.setLayout(new BorderLayout());
                                realP.setBackground(Color.white);
                                realP.setPreferredSize(new Dimension(297, 420));
                                real.setIcon(new ImageIcon(colorImage));
                                realP.add(real);
                                images.removeAll();
                                JPanel scaledP = new JPanel();
                                scaledP.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
                                scaledP.setLayout(new BorderLayout());
                                scaledP.setBackground(Color.white);
                                scaledP.setPreferredSize(new Dimension(297, 420));
                                scaled.setIcon(new ImageIcon(bw));
                                scaledP.add(scaled);
                                
                                images.add(realP);
                                images.add(scaledP);
                                
                                frame.revalidate();
                                frame.repaint();
                                brighten.setValue(1250);
                            } catch (IOException ex) {
                                Logger.getLogger(Vectorizer.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                });
                frame.setResizable(false);
                frame.setTitle("Choose image");
                frame.setLocation(5, 5);
                frame.setLayout(new BorderLayout());
                frame.getContentPane().add(gui, BorderLayout.CENTER);
                frame.getContentPane().add(LB, BorderLayout.PAGE_END);
                try {
                    UIManager.setLookAndFeel(UIManager.getInstalledLookAndFeels()[1].getClassName());
                    SwingUtilities.updateComponentTreeUI(frame);
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                    Logger.getLogger(Vectorizer.class.getName()).log(Level.SEVERE, null, ex);
                }
                frame.setVisible(true);
                ok.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        frame.dispose();
                        try {
                            Processer s = new Processer(bw);
                        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
                            Logger.getLogger(Vectorizer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } );
                frame.pack();
                frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
            }
        };
        SwingUtilities.invokeLater(r);
    }
    
    String extension(File f) {
        String fileName = f.getName();
        int indexFile = fileName.lastIndexOf('.');
        if(indexFile > 0 && indexFile < fileName.length() - 1)
            return fileName.substring(indexFile + 1);
        return "";
    }
}
