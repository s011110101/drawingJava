/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Java2dDrawingApplication;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

/**
 *
 * @author acv
 */
public class DrawingApplicationFrame extends JFrame
{

    // Create the panels for the top of the application. One panel for each
    private JPanel shapePanel;
    private JPanel optionPanel;
    
    // line and one to contain both of those panels.
    private JPanel topPanel;
    
    // create the widgets for the firstLine Panel.
    private JLabel shapeDesc;
    private JLabel directionLabel;
    private JButton colorButton1;
    private JButton colorButton2;
    private JButton undo;
    private JButton clear;
    //create the widgets for the secondLine Panel.
    private JLabel optionDesc;
    private JCheckBox fill;
    private JCheckBox gradient;
    private JCheckBox dash;
    private JLabel widthDesc;
    private JSpinner width;
    private JLabel lengthDesc;
    private JSpinner length;    
    private JFrame frame;
    private JComboBox<String> shapeComboBox;
    // Variables for drawPanel.
    private DrawPanel drawPanel;
    private Color color1;
    private Color color2;
    
    
    
    // add status label
    private JLabel statusLabel;
    private JPanel statusPanel;
    
    
    // Constructor for DrawingApplicationFrame
    public DrawingApplicationFrame()
    {
        setTitle("Java 2D Drawings");
        
        shapePanel = new JPanel();
        optionPanel = new JPanel();
        drawPanel = new DrawPanel();
        
        // firstLine widgets
        shapeDesc = new JLabel("Shape: ");
        shapeComboBox = new JComboBox<String>();
        shapeComboBox.addItem("Line");
        shapeComboBox.addItem("Oval");
        shapeComboBox.addItem("Rectangle");
        colorButton1 = new JButton("1st Color...");
        colorButton2 = new JButton("2nd Color...");
        undo = new JButton("Undo");
        clear = new JButton("Clear");
        // add widgets to panels
        shapePanel.add(shapeDesc);
        shapePanel.add(shapeComboBox);
        shapePanel.add(colorButton1);
        shapePanel.add(colorButton2);
        shapePanel.add(undo);
        shapePanel.add(clear);
        
        // secondLine widgets
        shapeDesc = new JLabel("Options: ");
        fill = new JCheckBox("Filled");
        gradient = new JCheckBox("Use Gradient");
        dash = new JCheckBox("Dashed");
        widthDesc = new JLabel("Line Width: ");
        width = new JSpinner(new SpinnerNumberModel(10, 1, 99, 1));
        lengthDesc = new JLabel("Dash Length: ");
        length = new JSpinner(new SpinnerNumberModel(10, 1, 99, 1));
        // add widgets to panels
        optionPanel.add(shapeDesc);
        optionPanel.add(fill);
        optionPanel.add(gradient);
        optionPanel.add(dash);
        optionPanel.add(widthDesc);
        optionPanel.add(width);
        optionPanel.add(lengthDesc);
        optionPanel.add(length);
        
        // add top panel of two panels
        topPanel = new JPanel(new BorderLayout());
        shapePanel.setBackground(Color.cyan);
        optionPanel.setBackground(Color.cyan);
        topPanel.setBackground(Color.cyan);
        topPanel.add(shapePanel,BorderLayout.NORTH);
        topPanel.add(optionPanel,BorderLayout.SOUTH);
        
        //status panel
        statusLabel = new JLabel("(null, null)");
        statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.add(statusLabel);
        statusPanel.setBackground(Color.lightGray);
        // add topPanel to North, drawPanel to Center, and statusLabel to South
        add(topPanel,BorderLayout.NORTH);
        add(drawPanel,BorderLayout.CENTER);
        add(statusPanel,BorderLayout.SOUTH);
        //add listeners and event handlers
        color1 = Color.white;
        colorButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JColorChooser colorChoose = new JColorChooser();
                Color temp = colorChoose.showDialog(DrawingApplicationFrame.this, "Choose second color", color2);
                if (temp!=null){
                        color1 = temp;
                }
            }
        });
        color2 = Color.white;
        colorButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JColorChooser colorChoose = new JColorChooser();
                Color temp = colorChoose.showDialog(DrawingApplicationFrame.this, "Choose second color", color2);
                if (temp!=null){
                        color2 = temp;
                }
            }
        });
        undo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.undoShape();
            }
        });
        clear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.clearShapes();
            }
        });
        
    }

    // Create event handlers, if needed

    // Create a private inner class for the DrawPanel.
    private class DrawPanel extends JPanel
    {
        private ArrayList<MyShapes> shapes = new ArrayList<>();
        private MyShapes currentShape;
        private MyShapes lastShape;
        
        public DrawPanel()
        {
            addMouseListener(new MouseHandler());
            addMouseMotionListener(new MouseHandler());
        }
        @Override
        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            //loop through and draw each shape in the shapes arraylist
                for (MyShapes shape : shapes) {
                shape.draw(g2d);
            }
        }
        public void clearShapes(){
            shapes.clear();
            repaint();
        }
        public void undoShape(){
            if (!shapes.isEmpty()){
                shapes.remove(shapes.size()-1);
            }
            repaint();
        }

        private class MouseHandler extends MouseAdapter implements MouseMotionListener
        {
            private Point startPoint;
            private Point endPoint;
            
            
            public void mousePressed(MouseEvent event)
            {
                startPoint = event.getPoint();
                endPoint = startPoint; 
                
                String selectedShape = (String) shapeComboBox.getSelectedItem();
                //BasicStroke stroke = new BasicStroke((Integer)width.getValue(),BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND, (Integer)length.getValue(), gradient.isSelected(),  dash.isSelected());
                BasicStroke stroke = new BasicStroke((Integer)width.getValue());
                float[] dashLength = {(Integer)length.getValue()};
                Paint paint = color1;
                if (gradient.isSelected()){
                    paint = new GradientPaint(0, 0, color1, 50, 50, color2, true);
                }
                
     
                if (dash.isSelected()){
                    stroke = new BasicStroke((Integer)width.getValue(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, dashLength, 0);
                } 
                else{
                    stroke = new BasicStroke((Integer)width.getValue(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                }

                if ("Line".equals(selectedShape)) {
                    currentShape = new MyLine(startPoint, endPoint, paint, stroke);
                } 
                else if ("Oval".equals(selectedShape)) {
                    currentShape = new MyOval(startPoint, endPoint, paint, stroke, fill.isSelected());
                } 
                else if ("Rectangle".equals(selectedShape)) {
                    currentShape = new MyRectangle(startPoint, endPoint, paint, stroke, fill.isSelected());
                }
            }
                
            
            
            public void mouseReleased(MouseEvent event)
            {
                if (currentShape != null) {
                    endPoint = event.getPoint();
                    currentShape.setEndPoint(endPoint);
                    shapes.add(currentShape);
                    currentShape = null;
                    repaint();
                }
            }

            @Override
            public void mouseDragged(MouseEvent event)
            {
                if (currentShape != null) {
                    endPoint = event.getPoint();
                    currentShape.setEndPoint(endPoint);
                    repaint(); 
                }
            }

            @Override
            public void mouseMoved(MouseEvent event)
            {
                statusLabel.setText("(" + event.getX() + ", " + event.getY() + ")");
            }
        }
    }
}
