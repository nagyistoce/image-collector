package team.nm.nnet.app.imageCollector.layout;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import sole.hawking.image.filter.EdgeFilter;
import team.nm.nnet.app.imageCollector.bo.FaceRecognitor;
import team.nm.nnet.app.imageCollector.bo.ImageDB;
import team.nm.nnet.app.imageCollector.om.DetectedFace;
import team.nm.nnet.app.imageCollector.om.FaceList;
import team.nm.nnet.app.imageCollector.om.Region;
import team.nm.nnet.app.imageCollector.support.ImageFilter;
import team.nm.nnet.app.imageCollector.support.NMFileFilter;
import team.nm.nnet.app.imageCollector.utils.Chooser;
import team.nm.nnet.app.imageCollector.utils.ColorSpace;
import team.nm.nnet.core.Const;
import team.nm.nnet.util.IOUtils;
import team.nm.nnet.util.ImageUtils;

public class MainFrame extends FaceList {

    private static final long serialVersionUID = 1L;
    
    private static MainFrame instance = null;
    private JFrame frame;
    
    private Capture capture;
    private SearchResult searchResult;
    private ImageDB imageDB;
    private BufferedImage showingImage = ImageUtils.load(Const.CURRENT_DIRECTORY + Const.RESOURCE_PATH + "authors.jpg");
    
    static ExecutorService executorService = Executors.newCachedThreadPool();
    private FaceRecognitor fileRecognitor;
    
    private MainFrame() {
    	frame = new JFrame("Dò Tìm và Nhận Dạng Khuôn Mặt - NM Team");
        initComponents();
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(frame);
        }
        catch (Exception ex) {
            throw new RuntimeException("Không thể 'look and feel'.");
        }

    	// Get the size of the screen
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

        // Determine the new location of the window
        int w = frame.getSize().width;
        int h = frame.getSize().height;
        int x = (dim.width-w)/2;
        int y = (dim.height-h)/2;
        frame.setLocation(x, y);
    }
    
    public static MainFrame getInstance() {
    	if(instance == null) {
    		instance = new MainFrame();
    	}
    	return instance;
    }
    

	@Override
	public void onAddingFace(DetectedFace face) {

		BufferedImage bufImg = face.getBufferedImage();
        
		ExtractedFacePanel facePanel = new ExtractedFacePanel(pnlFaces, ImageUtils.toImage(bufImg));
		facePanel.setFaceId(face.getFaceId());
		facePanel.setFaceName(face.getFaceName());
		
//		drawPlus(face.getRegion());
		drawRectangle(face.getRegion());
		pnlFaces.add(facePanel);
		pnlFaces.updateUI();
	}
	
	@Override
	public void onFulfiling() {
		lblProcess.setText("<html>Tìm thấy <span style='color:#FF0000'><b>" + getSize() + "</b></span> khuôn mặt</html>");
		lblProcess.setIcon(new ImageIcon(Const.CURRENT_DIRECTORY + Const.RESOURCE_PATH + "check.png"));
	}

	public void show(int width, int height) {
		if(frame == null) {
			return;
		}
		if(width > 1) {
			frame.setSize(width, frame.getHeight());
		} 
		if(height > 1) {
			frame.setSize(frame.getWidth(), height);
		}
		frame.setVisible(true);
	}

    public void displayImage(Image image, String imgName, long imgLength) {
    	BufferedImage bufferedImage = null;
    	if ((image == null) || (image.getWidth(null) < 1) || ((bufferedImage = ImageUtils.toBufferedImage(image)) == null)) {
        	JOptionPane.showMessageDialog(frame, "Không thể hiển thị ảnh này!", "Failed", JOptionPane.ERROR_MESSAGE);
            return;
        } else {
            showingImage = fitView(bufferedImage, lblImgView.getWidth(),
                    lblImgView.getHeight());
            lblImgName.setText(imgName);
            lblImgSize.setText(bufferedImage.getWidth(null) + "x"
                    + bufferedImage.getHeight(null) + " pixels");
            lblImgCap.setText(imgLength / 1024 + " KB");

            detectFaces();
        }
    	lblImgView.setIcon(new javax.swing.ImageIcon(showingImage));
    }

    // construct form view
    private void initComponents() {
    	
        java.awt.GridBagConstraints gridBagConstraints;

        frame.getContentPane().setLayout(new java.awt.GridBagLayout());
        frame.setMinimumSize(new java.awt.Dimension(950, 700));
        frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        frame.setIconImage(new ImageIcon(Const.CURRENT_DIRECTORY + Const.RESOURCE_PATH + "icon.png").getImage());
        frame.addWindowListener(new WindowAdapter() {
        	@Override
        	public void windowOpened(WindowEvent e) {
        		super.windowOpened(e);
        		onWindowOpened();
        	}
        	
        	@Override
        	public void windowClosing(WindowEvent e) {
        		super.windowClosing(e);
        		onWindowClosing();
        	}
		});

        jPanel5.setLayout(new java.awt.GridBagLayout());

        lblProcess.setText("Các khuôn mặt được tìm thấy");
        lblProcess.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(11, 10, 0, 0);
        jPanel5.add(lblProcess, gridBagConstraints);

        pnlFaces.setToolTipText("The faces are detected");
        pnlFaces.setBackground(new java.awt.Color(255, 255, 255));
        pnlFaces.setLayout(new javax.swing.BoxLayout(pnlFaces,
                javax.swing.BoxLayout.PAGE_AXIS));

        splFaces.setViewportView(pnlFaces);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 136;
        gridBagConstraints.ipady = 515;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(11, 10, 0, 0);
        jPanel5.add(splFaces, gridBagConstraints);

        btnSearchInDB.setText("Tìm ảnh trong CSDL");
        btnSearchInDB.setToolTipText("Search in DB");
        btnSearchInDB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchInDBActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(16, 20, 0, 0);
        jPanel5.add(btnSearchInDB, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(11, 6, 0, 0);
        frame.getContentPane().add(jPanel5, gridBagConstraints);

        lblImgView.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblImgView.setIcon(new javax.swing.ImageIcon(showingImage));
        lblImgView.setBorder(new javax.swing.border.MatteBorder(null));
        lblImgView.setPreferredSize(new java.awt.Dimension(3074, 2306));
        lblImgView.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(e.getButton() == MouseEvent.BUTTON3) {
					showBinaryImage();
				}
			}
		});

        jLabel2.setText("Ảnh mẫu");
        jLabel4.setText("Tập tin:");
        jLabel5.setText("Kích thước:");
        jLabel6.setText("Dung lượng:");

        btnSysFile.setText("Từ hệ thống");
        btnSysFile.setToolTipText("From system file");
        btnSysFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSysFileActionPerformed(evt);
            }
        });
        
        btnURLFile.setText("Từ Internet");
        btnURLFile.setToolTipText("From internet file");
        btnURLFile.addActionListener(new java.awt.event.ActionListener() {
        	public void actionPerformed(java.awt.event.ActionEvent evt) {
        		btnURLFileActionPerformed(evt);
        	}
        });

        btnWebcam.setText("Từ webcam");
        btnWebcam.setToolTipText("From webcam");
        btnWebcam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnWebcamActionPerformed(evt);
            }
        });

        lblImgName.setText("filename.ext");
        lblImgSize.setText("w x h pixels");
        lblImgCap.setText("? KB");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(
                jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout
                .setHorizontalGroup(jPanel7Layout
                        .createParallelGroup(
                                javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(
                                jPanel7Layout
                                        .createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                jPanel7Layout
                                                        .createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(
                                                                jPanel7Layout
                                                                        .createSequentialGroup()
                                                                        .addComponent(
                                                                                lblImgView,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                717,
                                                                                Short.MAX_VALUE)
                                                                        .addContainerGap())
                                                        .addGroup(
                                                                jPanel7Layout
                                                                        .createSequentialGroup()
                                                                        .addComponent(
                                                                                jLabel2)
                                                                        .addGap(80,
                                                                                80,
                                                                                80)
                                                                        .addComponent(
                                                                                btnSysFile,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                118,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addGap(109,
                                                                                109,
                                                                                109)
                                                                        .addComponent(
                                                                        		btnURLFile,
                                                                        		javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                        		118,
                                                                        		javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                		.addGap(109,
                                                                				109,
                                                                				109)
                                                                        .addComponent(
                                                                                btnWebcam,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                125,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addContainerGap())
                                                        .addGroup(
                                                                jPanel7Layout
                                                                        .createSequentialGroup()
                                                                        .addComponent(
                                                                                jLabel4)
                                                                        .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                        .addComponent(
                                                                                lblImgName)
                                                                        .addGap(80,
                                                                                80,
                                                                                80)
                                                                        .addComponent(
                                                                                jLabel5)
                                                                        .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                        .addComponent(
                                                                                lblImgSize)
                                                                        .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                                                296,
                                                                                Short.MAX_VALUE)
                                                                        .addComponent(
                                                                                jLabel6)
                                                                        .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                        .addComponent(
                                                                                lblImgCap)
                                                                        .addGap(47,
                                                                                47,
                                                                                47)))));
        jPanel7Layout
                .setVerticalGroup(jPanel7Layout
                        .createParallelGroup(
                                javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(
                                jPanel7Layout
                                        .createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                jPanel7Layout
                                                        .createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel2)
                                                        .addComponent(btnWebcam)
                                                        .addComponent(btnURLFile)
                                                        .addComponent(
                                                                btnSysFile))
                                        .addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(
                                                lblImgView,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                552,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addGroup(
                                                jPanel7Layout
                                                        .createParallelGroup(
                                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel5)
                                                        .addComponent(jLabel4)
                                                        .addComponent(
                                                                lblImgName)
                                                        .addComponent(
                                                                lblImgSize)
                                                        .addComponent(jLabel6)
                                                        .addComponent(lblImgCap))
                                        .addContainerGap()));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.ipady = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        frame.getContentPane().add(jPanel7, gridBagConstraints);

        jMenu1.setText("Tài Liệu");

        smnOpen_Image.setText("Chọn ảnh mẫu");
        smnOpen_Image.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                smnOpen_ImageActionPerformed(evt);
            }
        });
        jMenu1.add(smnOpen_Image);

        jMenu3.setText("Cơ Sở Dữ Liệu Ảnh");
        jMenu3.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuSelected(javax.swing.event.MenuEvent evt) {
            	boolean isOn = imageDB.getFiles().size() > 0;
            	smnDB_Show.setEnabled(isOn);
            	smnDB_Clear.setEnabled(isOn);
            	smnDB_Add.setEnabled(isOn);
            	smnDB_Save.setEnabled(isOn);
            }
        });
        
        smnDB_New.setText("Tạo mới");
        smnDB_New.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                List<File> files = Chooser.getMultiFiles("Tạo mới CSDL ảnh", new ImageFilter());
                if(CollectionUtils.isNotEmpty(files)) {
                	imageDB.setFiles(files);
                	JOptionPane.showMessageDialog(frame, files.size() + " ảnh đã được nạp vào CSDL!", "Succeed", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        jMenu3.add(smnDB_New);
        
        smnDB_Load.setText("Nạp từ tệp");
        smnDB_Load.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                File file = Chooser.getSingleFile("Nạp CSDL ảnh", new NMFileFilter());
                if(file != null) {
                	try {
                		JOptionPane.showMessageDialog(frame, imageDB.load(file) + " ảnh đã được nạp vào CSDL!", "Succeed", JOptionPane.INFORMATION_MESSAGE);
                	} catch(Exception e) {
                		JOptionPane.showMessageDialog(frame, "Không thể nạp CSDL ảnh từ tệp này!", "Failed", JOptionPane.ERROR_MESSAGE);
                	}
                }
            }
        });
        jMenu3.add(smnDB_Load);
        jMenu3.add(new JSeparator());
        
        smnDB_Show.setText("Xem");
        smnDB_Show.setEnabled(false);
        smnDB_Show.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	new DBViewer(imageDB).setVisible(true);
            }
        });
        jMenu3.add(smnDB_Show);
        jMenu3.add(new JSeparator());
        
        smnDB_Clear.setText("Xóa");
        smnDB_Clear.setEnabled(false);
        smnDB_Clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	int confirm = JOptionPane.showConfirmDialog(frame, "Bạn có chắc muốn xóa CSDL ảnh không!", "Clear image DB?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            	if(confirm == JOptionPane.YES_OPTION) {
            		imageDB.clear();
            	}
            }
        });
        jMenu3.add(smnDB_Clear);
        
        smnDB_Add.setText("Thêm");
        smnDB_Add.setEnabled(false);
        smnDB_Add.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	List<File> files = Chooser.getMultiFiles("Thêm ảnh vào CSDL", new ImageFilter());
                if(CollectionUtils.isNotEmpty(files)) {
                	imageDB.add(files);
                	JOptionPane.showMessageDialog(frame, "CSDL ảnh đã được cập nhật!", "Succeed", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        jMenu3.add(smnDB_Add);
        
        smnDB_Save.setText("Lưu lại");
        smnDB_Save.setEnabled(false);
        smnDB_Save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                File file = Chooser.save("Lưu CSDL ảnh", new NMFileFilter());
                if(file != null) {
                	try {
                		imageDB.save(file);
                		JOptionPane.showMessageDialog(frame, "CSDL ảnh đã lưu thành công!", "Succeed", JOptionPane.INFORMATION_MESSAGE);
                	} catch(Exception e) {
                		JOptionPane.showMessageDialog(frame, "không thể lưu CSDL ảnh!", "Failed", JOptionPane.ERROR_MESSAGE);
                	}
                }
            }
        });
        jMenu3.add(smnDB_Save);

        jMenu1.add(jMenu3);

        jMenuBar1.add(jMenu1);

        jMenu5.setText("Tùy Chọn");

        smnOption_Customize.setText("Tùy Chỉnh");
        smnOption_Customize.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CustomizeForm.getInstance().setVisible(true);
			}
            
        });
        jMenu5.add(smnOption_Customize);

        jMenuBar1.add(jMenu5);

        jMenu4.setText("Giới Thiệu");

        smnAbout_App.setText("Chương trình");
        smnAbout_App.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AboutApp.getInstance().setVisible(true);
            }
        });
        jMenu4.add(smnAbout_App);

        smnAbout_Us.setText("Thành viên");
        smnAbout_Us.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AboutUs.getInstance().setVisible(true);
            }
        });
        jMenu4.add(smnAbout_Us);

        jMenuBar1.add(jMenu4);

        frame.setJMenuBar(jMenuBar1);

        frame.pack();
    }
    
    private void btnSearchInDBActionPerformed(java.awt.event.ActionEvent evt) {
		List<Integer> wantedFaces = getWantedFace();
		List<File> inputs = imageDB.getFiles();
		
		if((inputs == null) || (inputs.size() < 1)){
			JOptionPane.showMessageDialog(frame, "Cơ sở dữ liệu ảnh chưa được chỉ định!", "Empty image database!", JOptionPane.ERROR_MESSAGE);
		} else if((wantedFaces == null) || (wantedFaces.size() < 1)){
			JOptionPane.showMessageDialog(frame, "Không có khuôn mặt nào được chỉ định!", "Not specified yet!", JOptionPane.ERROR_MESSAGE);
		} else {
			searchResult.search(imageDB.getFiles(), wantedFaces);
			searchResult.setVisible(true);
		}
    }

    private void smnOpen_ImageActionPerformed(java.awt.event.ActionEvent evt) {
    	btnSysFileActionPerformed(null);
    }

    private void btnSysFileActionPerformed(java.awt.event.ActionEvent evt) {
        File selectedFile = Chooser.getSingleFile("Lấy ảnh mẫu", new ImageFilter());
        if(selectedFile != null) {
            displayImage(new ImageIcon(selectedFile.getPath()).getImage(),
                    selectedFile.getName(), selectedFile.length());
        }
    }
    
    private void btnURLFileActionPerformed(java.awt.event.ActionEvent evt) {
    	String url = JOptionPane.showInputDialog(frame, "Đường link đến ảnh", "Lấy ảnh mẫu", JOptionPane.QUESTION_MESSAGE);
    	if(StringUtils.isNotBlank(url) && url.startsWith("http")) {
    		lblImgView.setIcon(new ImageIcon(Const.CURRENT_DIRECTORY + Const.RESOURCE_PATH + "loading.gif"));
    		displayImage(ImageUtils.loadURL(url), url.substring(url.lastIndexOf("/") + 1), 0);
    	}
    }

    private void btnWebcamActionPerformed(java.awt.event.ActionEvent evt) {
        capture.setParent(this);
        capture.show();
    }
    
    protected void showBinaryImage() {
    	final BufferedImage bufferedImage = ImageUtils.toBufferedImage(showingImage);
		final BufferedImage y2CBuff = ColorSpace.toYCbCr(bufferedImage);
		
    	final JFrame frm = new JFrame("Binary Image - YCbCr Model");
    	frm.setIconImage(new ImageIcon(Const.CURRENT_DIRECTORY + Const.RESOURCE_PATH + "icon.png").getImage());
		final JLabel lbl = new JLabel(new ImageIcon(ImageUtils.toImage(y2CBuff)));
		lbl.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(e.getButton() == MouseEvent.BUTTON1) {
					frm.setTitle("Binary Image - Opening processing");
					lbl.setIcon(new ImageIcon(ImageUtils.toImage(ColorSpace.getBinaryBuffer(bufferedImage))));
				} else if(e.getButton() == MouseEvent.BUTTON3) {
					frm.setTitle("Binary Image - Edge filtering");
				    EdgeFilter edgeFilter = new EdgeFilter();
				    lbl.setIcon(new ImageIcon(ImageUtils.toImage(edgeFilter.filter(y2CBuff, null))));
                }
			}
		});
		frm.add(lbl);
		frm.setSize(y2CBuff.getWidth(null), y2CBuff.getHeight(null));
		frm.setVisible(true);
    }

    private BufferedImage fitView(BufferedImage bufferedImage, int width, int height) {
        if ((bufferedImage.getWidth(null) > width)
                || (bufferedImage.getHeight(null)) > height) {
            bufferedImage = ImageUtils.scale(bufferedImage, width, height);
        }
        return bufferedImage;
    }

    private void detectFaces() {
        
    	if(fileRecognitor != null) {
    		fileRecognitor.requestStop();
        }

    	clear();
        pnlFaces.removeAll();
        pnlFaces.updateUI();
        System.gc();
        
        lblProcess.setText("Đang tìm...");
        lblProcess.setIcon(new ImageIcon(Const.CURRENT_DIRECTORY + Const.RESOURCE_PATH + "waiting.gif"));
        fileRecognitor.prepare();
        fileRecognitor.setFaceResults(this);
        executorService.execute(new Runnable() {
			@Override
			public void run() {
				fileRecognitor.recognize(showingImage);
			}
		});
    }
    
    protected List<Integer> getWantedFace() {
    	List<Integer> list = new ArrayList<Integer>();
    	Component[] components = pnlFaces.getComponents();
        for(Component comp : components) {
            ExtractedFacePanel item = (ExtractedFacePanel) comp;
            if(item.isSelected()) {
                list.add(item.getFaceId());
            }
        }
        return list;
    }
    
    protected void drawPlus(Region region) {
    	int x = region.getLeft() + region.getWidth() / 2;
    	int y = region.getBottom() + region.getHeight() / 2;
    	
    	for(int i = -2; i < 3; i++) {
    		for(int j = -2; j < 3; j++) {
    			showingImage.setRGB(x + i, y + j, Color.RED.getRGB());
    		}
    	}
    	lblImgView.setIcon(new javax.swing.ImageIcon(showingImage));
    }
    
    protected void drawRectangle(Region region) {
    	int left = region.getLeft(), right = region.getRight();
    	int bottom = region.getBottom(), top = region.getTop();
    	
    	for(int x = left; x < right; x++) {
    		showingImage.setRGB(x, bottom, Color.GREEN.getRGB());
    		showingImage.setRGB(x, top, Color.GREEN.getRGB());
    	}
    	for(int y = bottom; y < top; y++) {
    		showingImage.setRGB(left, y, Color.GREEN.getRGB());
    		showingImage.setRGB(right, y, Color.GREEN.getRGB());
    	}
    	
    }

	private static final String[] extendedLibs = {"civil.dll", "jdshow.dll"};
	private static final String libDestination = "c:/windows/system32";
	
    private void onWindowOpened() {
		IOUtils.copy(Const.CURRENT_DIRECTORY + Const.RESOURCE_PATH, extendedLibs, libDestination);
    }
    
    private void onWindowClosing() {
    	capture.close();
    	IOUtils.delete(libDestination, extendedLibs);
    }

    // Variables declaration
    private javax.swing.JButton btnSearchInDB = new JButton();
    private javax.swing.JButton btnSysFile = new JButton();
    private javax.swing.JButton btnURLFile = new JButton();
    private javax.swing.JButton btnWebcam = new JButton();
    private javax.swing.JLabel jLabel2 = new JLabel();
    private javax.swing.JLabel lblProcess = new JLabel();
    private javax.swing.JLabel jLabel4 = new JLabel();
    private javax.swing.JLabel jLabel5 = new JLabel();
    private javax.swing.JLabel jLabel6 = new JLabel();
    private javax.swing.JLabel lblImgCap = new JLabel();
    private javax.swing.JLabel lblImgName = new JLabel();
    private javax.swing.JLabel lblImgSize = new JLabel();
    private javax.swing.JLabel lblImgView = new JLabel();
    private javax.swing.JMenu jMenu1 = new JMenu();
    private javax.swing.JMenu jMenu3 = new JMenu();
    private javax.swing.JMenu jMenu4 = new JMenu();
    private javax.swing.JMenu jMenu5 = new JMenu();
    private javax.swing.JMenuBar jMenuBar1 = new JMenuBar();
    private javax.swing.JMenuItem smnAbout_App = new JMenuItem();
    private javax.swing.JMenuItem smnAbout_Us = new JMenuItem();
    private javax.swing.JMenuItem smnOption_Customize = new JMenuItem();
    private javax.swing.JMenuItem smnDB_New = new JMenuItem();
    private javax.swing.JMenuItem smnDB_Load = new JMenuItem();
    private javax.swing.JMenuItem smnDB_Show = new JMenuItem();
    private javax.swing.JMenuItem smnDB_Clear = new JMenuItem();
    private javax.swing.JMenuItem smnDB_Add = new JMenuItem();
    private javax.swing.JMenuItem smnDB_Save = new JMenuItem();
    private javax.swing.JMenuItem smnOpen_Image = new JMenuItem();
    private javax.swing.JScrollPane splFaces = new JScrollPane();
    private javax.swing.JPanel pnlFaces = new JPanel();
    private javax.swing.JPanel jPanel5 = new JPanel();
    private javax.swing.JPanel jPanel7 = new JPanel();

    // End of variables declaration

    @Required
    public void setCapture(Capture capture) {
        this.capture = capture;
    }

    @Required
	public void setImageDB(ImageDB imageDB) {
		this.imageDB = imageDB;
	}

    @Required
	public void setFileRecognitor(FaceRecognitor fileRecognitor) {
		this.fileRecognitor = fileRecognitor;
	}

    @Required
	public void setSearchResult(SearchResult searchResult) {
		this.searchResult = searchResult;
	}

}
