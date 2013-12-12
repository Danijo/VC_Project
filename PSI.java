import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.*;
import java.io.*;
import java.math.*;

// ---------------------------------------------------------------
// Classe que cria uma Frame principal, onde se situam os comandos
// de manipula��o de imagem. Implementa a interface ActionListener
// para lidar com os eventos produzidos pelos bot�es.
// ---------------------------------------------------------------
class PSI extends Frame implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	// Vari�veis globais de apoio
	// Aten��o: E se eu quiser ter m�ltiplas imagens?
	// Isto deve estar na classe ImageFrame!
	private Image image;
	private int sizex;
	private int sizey;;
	private int matrix[];
	ImagePanel imagePanel; // E se eu quiser m�ltiplas janelas?
	
	// Fun��o main cria uma instance din�mica da classe
	public static void main(String args[])
	{
		new PSI();
	}

	// Construtor
	public PSI()
	{
		// Lidar com o evento de Fechar Janela
		addWindowListener(new WindowAdapter() {
      		public void windowClosing(WindowEvent e) {
        		System.exit(0);
      		}
		});

		// Sinalizar que n�o existe nenhum ImagePanel
		imagePanel = null;
		
		// Criar bot�es 
		this.setLayout(new GridLayout(3,1,1,1));
		
		Button button = new Button("Abrir Ficheiro");
		button.setVisible(true);
		button.addActionListener(this);
		add(button);		

		button = new Button("Manipular Imagem");
		button.setVisible(true);
		button.addActionListener(this);
		add(button);		

		button = new Button("Guardar Imagem");
		button.setVisible(true);
		button.addActionListener(this);
		add(button);		
		
		button = new Button("Green");
		button.setVisible(true);
		button.addActionListener(this);
		add(button);	
		
		button = new Button("Red");
		button.setVisible(true);
		button.addActionListener(this);
		add(button);
		
		button = new Button("Blue");
		button.setVisible(true);
		button.addActionListener(this);
		add(button);
		
		button = new Button("Saturacao");
		button.setVisible(true);
		button.addActionListener(this);
		add(button);
		
		pack();
		
		// Janela principal 	
		setLocation(100,100);
		setSize(100,150);
		setVisible(true);
	}
	
	
	// O utilizador carregou num bot�o
	public void actionPerformed (ActionEvent myEvent)
	{
		// Qual o bot�o premido?
		Button pressedButton = (Button)myEvent.getSource();
		String nomeBotao = pressedButton.getActionCommand();

		// Realizar ac��o adequada
		if (nomeBotao.equals("Abrir Ficheiro")) abrirFicheiro();
		else if (nomeBotao.equals("Manipular Imagem")) manipularImagem();
		else if (nomeBotao.equals("Guardar Imagem")) guardarImagem();
		else if (nomeBotao.equals("Green")) changeColor("green");
		else if (nomeBotao.equals("Red")) changeColor("red");
		else if (nomeBotao.equals("Blue")) changeColor("blue");
		else if (nomeBotao.equals("Saturacao")) mudarS();
	}
	
	
	private void mudarS(){
		
		int x;
		int verde = 0, vermelho = 0, azul = 0, cinzento;
		int saturacao;

			
		for (x=0; x < sizex*sizey; x++)
		{
			// Vari�veis de apoio
			vermelho = getRed(matrix[x]);
			verde = getGreen(matrix[x]);
			azul = getBlue(matrix[x]);
			
		}
			
		//Saturacao
		saturacao = 1-(3/(vermelho+verde+azul));
		
					
		if (vermelho > verde){
			if (vermelho > azul){
				saturacao = 1-(3/(vermelho+verde+azul))*vermelho;
			}
		}
		else 
			if (verde > azul)
				saturacao = 1-(3/(vermelho+verde+azul))*verde;
			else
				saturacao = 1-(3/(vermelho+verde+azul))*azul;
		
		matrix[x] = makeColor(saturacao, saturacao, saturacao);
		
		// Ap�s a manipula�ao da matrix, � necess�rio criar o objecto gr�fico (image) 
		image = createImage(new MemoryImageSource(sizex, sizey, matrix, 0, sizex));
		
		// Carregar a imagem no painel externo de visualiza��o
		imagePanel.newImage(image);
	}
	
	private void changeColor(String c){
		// Vari�veis de apoio
				int verde, vermelho, azul, cinzento;
				int x;

				// Ciclo que percorre a imagem inteira
				for (x=0; x < sizex*sizey; x++)
				{
					vermelho = getRed(matrix[x]);
					verde = getGreen(matrix[x]);
					azul = getBlue(matrix[x]);
					
					// Calcular luminosidade
					cinzento = (vermelho+verde+azul)/3;
					
					// Criar valor de cor
					if (c.equals("green")){
						matrix[x] = makeColor(0, verde, 0);
					}
					if (c.equals("red")){
						matrix[x] = makeColor(vermelho, 0, 0);
					}
					if (c.equals("blue")){
						matrix[x] = makeColor(0, 0, azul);
					}
					
					
				}
				
				// Ap�s a manipula�ao da matrix, � necess�rio criar o objecto gr�fico (image) 
				image = createImage(new MemoryImageSource(sizex, sizey, matrix, 0, sizex));
				
				// Carregar a imagem no painel externo de visualiza��o
				imagePanel.newImage(image);
	}
	
	// Abrir um ficheiro de Imagem
	private void abrirFicheiro()
	{
		// Load Image - Escolher nome da imagem a carregar!
		// Bem mais interessante usar uma interface gr�fica para isto...
		LoadImage("lena.jpg");
	
		// Obter matriz da imagem
		// A vari�vel "matrix" fica com os valores de cada pixel da imagem
		// A dimens�o desta � determinada por "sizex" e "sizey"
		// Cada valor t�m 4 bytes. Estes correspondem invidividualmente a:
		// Transpar�ncia, Vermelho, Verde, Azul
		// Para aceder aos valores individuais:
		//		red = (color >> 16) & 0xff;
		//	    green = (color >> 8) & 0xff;
		//		blue = color & 0xff;
		sizex = image.getWidth(null);
		sizey = image.getHeight(null);
		matrix = new int[sizex*sizey];
		PixelGrabber pg = new PixelGrabber(image, 0, 0, sizex, sizey, matrix, 0, sizex);
		try {
		    pg.grabPixels();
		} catch (InterruptedException e) {
		    System.err.println("interrupted waiting for pixels!");
		    return;
		}
		if ((pg.getStatus() & ImageObserver.ABORT) != 0) {
		    System.err.println("image fetch aborted or errored");
		    return;
		}
		
		
		// Visualizar imagem - Usar um Frame externo
		if (imagePanel==null) imagePanel = new ImagePanel(image);
		else imagePanel.newImage(image);
		imagePanel.setLocation(300, 200);
		imagePanel.setSize(image.getWidth(null),image.getHeight(null));
		imagePanel.setVisible(true);
	}

	// Exemplo de uma fun��o que manipula a imagem
	public void manipularImagem()
	{
		// Exemplo: Convers�o de uma imagem a cores, para uma imagem a preto e branco

		// Vari�veis de apoio
		int verde, vermelho, azul, cinzento, vermelho1, verde1, azul1, vermelho2, verde2, azul2;
		int x, y, z;

		
		// Ciclo que percorre a imagem inteira
		for (x=0; x < sizex*sizey; x++)
		{
			vermelho = getRed(matrix[x]);
			verde = getGreen(matrix[x]);
			azul = getBlue(matrix[x]);
			
			// Calcular luminosidade
			cinzento = (vermelho+verde+azul)/3;
			
			// Criar valor de cor
			matrix[x] = makeColor(cinzento, cinzento, cinzento);
		}
		
		
		
		
		
		
		for (x=1; x < (sizey-1); x++)
		{
			int tmp = x * sizex;
			//System.out.println(x);
			
			
			for (y = 1; y < (sizex-1); y++){
				z = tmp+y;
	
				
				vermelho = (getRed(matrix[z]) + getRed(matrix[z+1]) + getRed(matrix[z-1]) +  getRed(matrix[z-(sizex-1)]) + getRed(matrix[z-sizex]) + getRed(matrix[z-(sizex+1)]) + getRed(matrix[z+(sizex-1)]) + getRed(matrix[z+sizex]) + getRed(matrix[z+(sizex+1)])) / 9;
				verde = (getGreen(matrix[z]) + getGreen(matrix[z+1]) + getGreen(matrix[z-1]) +  getGreen(matrix[z-(sizex-1)]) + getGreen(matrix[z-sizex]) + getGreen(matrix[z-(sizex+1)]) + getGreen(matrix[z+(sizex-1)]) + getGreen(matrix[z+sizex]) + getGreen(matrix[z+(sizex+1)])) / 9;
				azul = (getBlue(matrix[z]) + getBlue(matrix[z+1]) + getBlue(matrix[z-1]) +  getBlue(matrix[z-(sizex-1)]) + getBlue(matrix[z-sizex]) + getBlue(matrix[z-(sizex+1)]) + getBlue(matrix[z+(sizex-1)]) + getBlue(matrix[z+sizex]) + getBlue(matrix[z+(sizex+1)])) / 9;
			

				matrix[z] = makeColor(vermelho, verde, azul);
				//matrix[z] = makeColor(255, 255, 255);
				//System.out.println(z);
			}
		}
		
		
		for (x=1; x < (sizey-1); x++)
		{
			int tmp = x * sizex;
			//System.out.println(x);
				
			for (y = 1; y < (sizex-1); y++){
				z = tmp+y;
	
				vermelho1 = getRed(matrix[z-(sizex-1)]) + (getRed(matrix[z-sizex]) * 2) + getRed(matrix[z-(sizex+1)]) - (getRed(matrix[z+(sizex-1)]) +  (getRed(matrix[z+sizex]) * 2)  + getRed(matrix[z+(sizex+1)]));
				verde1    = getGreen(matrix[z-(sizex-1)]) + (getGreen(matrix[z-sizex]) * 2) + getGreen(matrix[z-(sizex+1)]) - (getGreen(matrix[z+(sizex-1)]) + (getGreen(matrix[z+sizex]) * 2) + getGreen(matrix[z+(sizex+1)]));
				azul1     = getBlue(matrix[z-(sizex-1)]) + (getBlue(matrix[z-sizex]) * 2) + getBlue(matrix[z-(sizex+1)]) - (getBlue(matrix[z+(sizex-1)]) + (getBlue(matrix[z+sizex]) * 2) + getBlue(matrix[z+(sizex+1)]));
						
				vermelho2 = getRed(matrix[z-(sizex+1)]) + (getRed(matrix[z+1]) * 2) + getRed(matrix[z+(sizex+1)]) - (getRed(matrix[z-(sizex-1)]) + (getRed(matrix[z-1]) * 2) + getRed(matrix[z+(sizex-1)]));
				verde2    = getGreen(matrix[z-(sizex+1)]) + (getGreen(matrix[z+1]) * 2) + getGreen(matrix[z+(sizex+1)]) - (getGreen(matrix[z-(sizex-1)]) + (getGreen(matrix[z-1]) * 2) + getGreen(matrix[z+(sizex-1)]));
				azul2     = getBlue(matrix[z-(sizex+1)]) + (getBlue(matrix[z+1]) * 2) + getBlue(matrix[z+(sizex+1)]) - (getBlue(matrix[z-(sizex-1)]) + (getBlue(matrix[z-1]) * 2) + getBlue(matrix[z+(sizex-1)]));

				
				int red = (vermelho1*vermelho1) + (vermelho2*vermelho2);
				int green = (verde1*verde1) + (verde2*verde2);
				int blue = (azul1*azul1) + (azul2*azul2);
						
				double r = Math.sqrt(red);
				double g = Math.sqrt(green);
				double b = Math.sqrt(blue);
			
				System.out.print(r);
				System.out.print(" ");
				System.out.print(g);
				System.out.print(" ");
				System.out.println(b);
				
				if (r > 255) {r = 255;}
				if (g > 255) {g = 255;}
				if (b > 255) {b = 255;}
				
				
				if (r < 0) {r = 0;}
				if (g < 0) {g = 0;}
				if (b < 0) {b = 0;}
				
				
				//matrix[z] = makeColor(red,green,blue);
				matrix[z] = makeColor((int) r,(int) g ,(int) b);
			}
		}
			
		
		
		// Ap�s a manipula�ao da matrix, � necess�rio criar o objecto gr�fico (image) 
		image = createImage(new MemoryImageSource(sizex, sizey, matrix, 0, sizex));
		
		// Carregar a imagem no painel externo de visualiza��o
		imagePanel.newImage(image);
	}

	// Fun��o de apoio que grava a imagem visualizada
	private void guardarImagem()
	{
		// Criar uma BufferedImage a partir de uma Image
		BufferedImage bi = new BufferedImage(image.getWidth(null),image.getHeight(null),BufferedImage.TYPE_INT_RGB);
	    Graphics bg = bi.getGraphics();
	    bg.drawImage(image, 0, 0, null);
	    bg.dispose();
	    
	    // Escrever ficheiro de sa�da
	    // Pq n�o implementar uma interface de escolha do nome?
		try {
			ImageIO.write(bi, "jpg", new File("resultado.jpg"));
		} catch (IOException e) {
		    System.err.println("Couldn't write output file!");
		    return;
		}
	}
	
	// Fun��o de apoio que carrega uma imagem externa
	public void LoadImage(String fileName) {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		image = toolkit.getImage(fileName);
		MediaTracker mediaTracker = new MediaTracker(this);
		mediaTracker.addImage(image, 0);
		try { mediaTracker.waitForID(0); }
		catch (InterruptedException ie) {};		
	}

	// Fun��es de apoio para extrair os valores de R, G e B de uma imagem.
	private int getRed(int color) { return (color >> 16) & 0xff; }
	private int getGreen(int color) { return (color >> 8) & 0xff; }
	private int getBlue(int color) { return color & 0xff; }
	private int makeColor(int red, int green, int blue) { return (255 << 24) | (red << 16) | (green << 8) | blue; }
}

//---------------------------------------------------------------
// Classe Frame de apoio para visualiza��o de uma imagem
//--------------------------------------------------------------- 
class ImagePanel extends Frame
{
	private static final long serialVersionUID = 1L;
	private Image image; 
	
	// Construtor
	public ImagePanel(Image newImage)
	{
		image = newImage;

		// Handle close window button
		addWindowListener(new WindowAdapter() {
	  		public void windowClosing(WindowEvent e) {
	    		System.exit(0);
	  		}
		});
	}

	// Carregar nova imagem no ImagePanel
	public void newImage(Image im)
	{
		image = im;
		repaint();
	}
	
	// Desenhar imagem 
	public void paint(Graphics g) {
		g.drawImage(image, 0, 0, null);
		super.paint(g);
	}
}