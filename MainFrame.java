import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class Board extends JPanel {
	private final int
    	WIDTH = 10,
        HEIGHT = 20,
        BLOCK_SIZE = 20,
        
        TIEMPO_ANIMACION = 200,
        T = 30, // épsilon en los documentos
        
        BLOQUE_VACIO = 0,
        PIEZA_T = 1,
        PIEZA_J = 2,
        PIEZA_L = 3,
        PIEZA_Z = 4,
        PIEZA_S = 5,
        PIEZA_I = 6,
        PIEZA_O = 7,
        
        COORDS_PIEZA_ACTUAL[][] = new int[2][4],
        COORDS_PIEZA_PROVISORIA[][] = new int[2][4],
        X = 0,
        Y = 1,
        PIVOTE = 0,
        LINEAS_COMPLETAS[] = new int[4],
        VALOR_NULO_LINEA = -1,
        
        // POSIBILITAR CAMBIO DE CONTROLES
        NO_COMANDO = -1,
        MOVER_IZQUIERDA = KeyEvent.VK_A,
        MOVER_DERECHA = KeyEvent.VK_D,
        HARD_DROP = KeyEvent.VK_S,
        ROTAR_HORARIO = KeyEvent.VK_E,
        ROTAR_ANTIHORARIO = KeyEvent.VK_Q;
        
    private final boolean
    	HORARIO = false,
        ANTIHORARIO = true;
        
   	private final Color BOARD[][] = new Color[WIDTH][HEIGHT];
    
    private final Color COLORS[] = {
    	Color.BLACK,	// la casilla vacía
        Color.WHITE,	// T
        Color.BLUE,		// J
        Color.YELLOW,	// L
        Color.RED,		// Z
        Color.GREEN,	// S
        Color.CYAN,		// I
        Color.ORANGE	// O
    };
    
    private int 
    	tiempo,
    	tiempo_caida,
    	pieza_actual_indice_color,
        comando_actual,
        keyframe;
        
    private boolean modo_animacion;
    
    private final Timer GAME_LOOP = new Timer(T, e -> ciclo());
    
    public Board() {
    	setPreferredSize(new Dimension(BLOCK_SIZE*WIDTH, BLOCK_SIZE*HEIGHT));
        setFocusable(true);
        requestFocus();
        tiempo = keyframe = 0;
        tiempo_caida = 500;
        modo_animacion = false;
        
        addKeyListener(new KeyAdapter() {
        	public void keyPressed(KeyEvent e) {
            	comando_actual = e.getKeyCode();
            }
            
            public void keyReleased(KeyEvent e) {
		// Clumsy solution
            	if (comando_actual == HARD_DROP) comando_actual = NO_COMANDO;
            }
        });

		// inicializando colores del tablero en negro
        for (int i = 0; i < WIDTH; i++)
        	for (int j = 0; j < HEIGHT; j++)
            	BOARD[i][j] = COLORS[BLOQUE_VACIO];
                
        // inicializando lista de lineas completas
        limpiarLineasCompletas();
        
        generarPieza();
        
        GAME_LOOP.start();
    }
    
    private void agregarLineaCompleta(int linea) {
    	for (int i = 0; i < 4; i++)
        	if (LINEAS_COMPLETAS[i] == VALOR_NULO_LINEA) {
            	LINEAS_COMPLETAS[i] = linea;
                break;
            }
    }
    
    private void limpiarLineasCompletas() {
    	for (int i = 0; i < 4; i++) LINEAS_COMPLETAS[i] = VALOR_NULO_LINEA;
    }
    
    private void generarPieza() {
    	pieza_actual_indice_color = (int)(Math.random() * 300 + 1) % 7 + 1;
        
        COORDS_PIEZA_ACTUAL[X][0] = 5;
        COORDS_PIEZA_ACTUAL[Y][0] = 0;
        
        switch (pieza_actual_indice_color) {
        	case PIEZA_T:
            	COORDS_PIEZA_ACTUAL[X][1] = COORDS_PIEZA_ACTUAL[X][PIVOTE] - 1;
        		COORDS_PIEZA_ACTUAL[X][2] = COORDS_PIEZA_ACTUAL[X][PIVOTE];
        		COORDS_PIEZA_ACTUAL[X][3] = COORDS_PIEZA_ACTUAL[X][PIVOTE] + 1;
                
        		COORDS_PIEZA_ACTUAL[Y][1] = COORDS_PIEZA_ACTUAL[Y][PIVOTE];
        		COORDS_PIEZA_ACTUAL[Y][2] = COORDS_PIEZA_ACTUAL[Y][PIVOTE] - 1;
        		COORDS_PIEZA_ACTUAL[Y][3] = COORDS_PIEZA_ACTUAL[Y][PIVOTE];
                
            	break;
                
            case PIEZA_J:
            	COORDS_PIEZA_ACTUAL[X][1] = COORDS_PIEZA_ACTUAL[X][PIVOTE] - 1;
        		COORDS_PIEZA_ACTUAL[X][2] = COORDS_PIEZA_ACTUAL[X][PIVOTE] + 1;
        		COORDS_PIEZA_ACTUAL[X][3] = COORDS_PIEZA_ACTUAL[X][PIVOTE] + 1;
                
        		COORDS_PIEZA_ACTUAL[Y][1] = COORDS_PIEZA_ACTUAL[Y][PIVOTE];
        		COORDS_PIEZA_ACTUAL[Y][2] = COORDS_PIEZA_ACTUAL[Y][PIVOTE];
        		COORDS_PIEZA_ACTUAL[Y][3] = COORDS_PIEZA_ACTUAL[Y][PIVOTE] + 1;
                
                break;
                
            case PIEZA_L:
            	COORDS_PIEZA_ACTUAL[X][1] = COORDS_PIEZA_ACTUAL[X][PIVOTE] - 1;
        		COORDS_PIEZA_ACTUAL[X][2] = COORDS_PIEZA_ACTUAL[X][PIVOTE] + 1;
        		COORDS_PIEZA_ACTUAL[X][3] = COORDS_PIEZA_ACTUAL[X][PIVOTE] - 1;
                
        		COORDS_PIEZA_ACTUAL[Y][1] = COORDS_PIEZA_ACTUAL[Y][PIVOTE];
        		COORDS_PIEZA_ACTUAL[Y][2] = COORDS_PIEZA_ACTUAL[Y][PIVOTE];
        		COORDS_PIEZA_ACTUAL[Y][3] = COORDS_PIEZA_ACTUAL[Y][PIVOTE] + 1;
                
                break;
                
             case PIEZA_Z:
            	COORDS_PIEZA_ACTUAL[X][1] = COORDS_PIEZA_ACTUAL[X][PIVOTE] + 1;
        		COORDS_PIEZA_ACTUAL[X][2] = COORDS_PIEZA_ACTUAL[X][PIVOTE] + 1;
        		COORDS_PIEZA_ACTUAL[X][3] = COORDS_PIEZA_ACTUAL[X][PIVOTE] + 2;
                
        		COORDS_PIEZA_ACTUAL[Y][1] = COORDS_PIEZA_ACTUAL[Y][PIVOTE];
        		COORDS_PIEZA_ACTUAL[Y][2] = COORDS_PIEZA_ACTUAL[Y][PIVOTE] - 1;
        		COORDS_PIEZA_ACTUAL[Y][3] = COORDS_PIEZA_ACTUAL[Y][PIVOTE] - 1;
             
             	break;
                
             case PIEZA_S:
            	COORDS_PIEZA_ACTUAL[X][1] = COORDS_PIEZA_ACTUAL[X][PIVOTE] - 1;
        		COORDS_PIEZA_ACTUAL[X][2] = COORDS_PIEZA_ACTUAL[X][PIVOTE] - 1;
        		COORDS_PIEZA_ACTUAL[X][3] = COORDS_PIEZA_ACTUAL[X][PIVOTE] - 2;
                
        		COORDS_PIEZA_ACTUAL[Y][1] = COORDS_PIEZA_ACTUAL[Y][PIVOTE];
        		COORDS_PIEZA_ACTUAL[Y][2] = COORDS_PIEZA_ACTUAL[Y][PIVOTE] - 1;
        		COORDS_PIEZA_ACTUAL[Y][3] = COORDS_PIEZA_ACTUAL[Y][PIVOTE] - 1;
                
             	break;
                
             case PIEZA_I:
            	COORDS_PIEZA_ACTUAL[X][1] = COORDS_PIEZA_ACTUAL[X][PIVOTE] + 1;
        		COORDS_PIEZA_ACTUAL[X][2] = COORDS_PIEZA_ACTUAL[X][PIVOTE] - 1;
        		COORDS_PIEZA_ACTUAL[X][3] = COORDS_PIEZA_ACTUAL[X][PIVOTE] - 2;
                
        		COORDS_PIEZA_ACTUAL[Y][1] = COORDS_PIEZA_ACTUAL[Y][PIVOTE];
        		COORDS_PIEZA_ACTUAL[Y][2] = COORDS_PIEZA_ACTUAL[Y][PIVOTE];
        		COORDS_PIEZA_ACTUAL[Y][3] = COORDS_PIEZA_ACTUAL[Y][PIVOTE];
                
             	break;
                
             case PIEZA_O:
            	COORDS_PIEZA_ACTUAL[X][1] = COORDS_PIEZA_ACTUAL[X][PIVOTE] + 1;
        		COORDS_PIEZA_ACTUAL[X][2] = COORDS_PIEZA_ACTUAL[X][PIVOTE] + 1;
        		COORDS_PIEZA_ACTUAL[X][3] = COORDS_PIEZA_ACTUAL[X][PIVOTE];
                
        		COORDS_PIEZA_ACTUAL[Y][1] = COORDS_PIEZA_ACTUAL[Y][PIVOTE];
        		COORDS_PIEZA_ACTUAL[Y][2] = COORDS_PIEZA_ACTUAL[Y][PIVOTE] - 1;
        		COORDS_PIEZA_ACTUAL[Y][3] = COORDS_PIEZA_ACTUAL[Y][PIVOTE] - 1;
                
             	break;
        }
    }
    
    private void copiarAPiezaProvisoria() {
        for (int i = 0; i < 4; i++) {
            COORDS_PIEZA_PROVISORIA[X][i] = COORDS_PIEZA_ACTUAL[X][i];
            COORDS_PIEZA_PROVISORIA[Y][i] = COORDS_PIEZA_ACTUAL[Y][i];
        }
    }
    
    private void copiarAPiezaActual() {
        for (int i = 0; i < 4; i++) {
            COORDS_PIEZA_ACTUAL[X][i] = COORDS_PIEZA_PROVISORIA[X][i];
            COORDS_PIEZA_ACTUAL[Y][i] = COORDS_PIEZA_PROVISORIA[Y][i];
        }
    }
    
    private boolean puedeSeguirCayendo() {
    	for (int i = 0; i < 4; i++) {
        	if (
            	COORDS_PIEZA_ACTUAL[Y][i] == HEIGHT-1
            ||	BOARD[COORDS_PIEZA_ACTUAL[X][i]][COORDS_PIEZA_ACTUAL[Y][i]+1] != Color.BLACK)
            		return false;
        }
        return true;
    }
    
    private void fijarPiezaActual() {
    	for (int i = 0; i < 4; i++) {
        	BOARD[COORDS_PIEZA_ACTUAL[X][i]][COORDS_PIEZA_ACTUAL[Y][i]] 
            	= COLORS[pieza_actual_indice_color];
        }
    }
    
    private void moverPiezaActual(int direccion) {
    	copiarAPiezaProvisoria();
        
    	for (int i = 0; i < 4; i++) {
        	COORDS_PIEZA_PROVISORIA[X][i] += Math.pow(-1, direccion == MOVER_IZQUIERDA? 1 : 0);
            if (COORDS_PIEZA_PROVISORIA[X][i] < 0 || COORDS_PIEZA_PROVISORIA[X][i] >= WIDTH
            ||	BOARD[COORDS_PIEZA_PROVISORIA[X][i]][COORDS_PIEZA_PROVISORIA[Y][i]] != Color.BLACK)
               	return;
        }
        
        copiarAPiezaActual();
    }
    
    private void procesarEntrada() {
    	switch (comando_actual) {
        	case MOVER_IZQUIERDA:
            case MOVER_DERECHA:
            	moverPiezaActual(comando_actual);
                comando_actual = NO_COMANDO; // DAS
                break;
                
            case HARD_DROP:
            	tiempo = tiempo_caida;
                break;
                
            case ROTAR_HORARIO:
            case ROTAR_ANTIHORARIO:
            	rotar(comando_actual == ROTAR_ANTIHORARIO);
                comando_actual = NO_COMANDO; // DAS
                break;
            default:
            	return;
        }
        
        repaint();
    }
    
    private void validarLineas() {
    	for (int y = HEIGHT-1; y >= 0; y--) {
        	for (int x = 0; x < WIDTH; x++) {
            	if (BOARD[x][y] == Color.BLACK) break;
                else if (x == WIDTH-1) {
                	agregarLineaCompleta(y);
                    modo_animacion = true;
                }
            }
        }
    }
    
	private boolean esLineaCompleta(int linea) {
    	for (int l = 0; l < 4 && LINEAS_COMPLETAS[l] != VALOR_NULO_LINEA; l++)
        	if (LINEAS_COMPLETAS[l] == linea) return true;
        return false;
    }
    
    private void ejecutarKeyframeSiguiente() {
    	if (keyframe < WIDTH) {
        	for (int i = 0; i < 4 && LINEAS_COMPLETAS[i] != VALOR_NULO_LINEA; i++)
            	BOARD[keyframe][LINEAS_COMPLETAS[i]] = COLORS[BLOQUE_VACIO];
            keyframe++;
        }
                
        else {
        	for (int y = HEIGHT-1, y_ = HEIGHT-1; y_ >= 0; y--) {
                if (!esLineaCompleta(y)) {
                    for (int x = 0; x < WIDTH; x++)
                        BOARD[x][y_] = y >= 0 ? BOARD[x][y] : COLORS[BLOQUE_VACIO];
                    y_--;
                }
            }
            limpiarLineasCompletas();
            keyframe = 0;
            modo_animacion = false;
        }
    }
    
    private void ciclo() {
    	tiempo += T;
		if (modo_animacion) {
			if (tiempo >= TIEMPO_ANIMACION)
				ejecutarKeyframeSiguiente();
                repaint();
		}
		else {
        	procesarEntrada();
          	if (tiempo >= tiempo_caida) {
            	if (puedeSeguirCayendo()) 
                	for (int i = 0; i < 4; i++)
                    	COORDS_PIEZA_ACTUAL[Y][i]++;
            	else {
                	fijarPiezaActual();
                	validarLineas();
                	generarPieza();
              	}
              	repaint();
              	tiempo = 0;
          }
		}        
//		else tiempo += T;
    }
    
    private void rotar(boolean s_antihorario) {
    	boolean esta_acostada;
        copiarAPiezaProvisoria();
        
        switch (pieza_actual_indice_color) {
        	case PIEZA_T:
            	for (int i = 1; i <= 3; i++) rotacionOrdinaria(s_antihorario, i);
                break;
                
            case PIEZA_J:
            case PIEZA_L:
            	rotacionOrdinaria(s_antihorario, 1);
                rotacionOrdinaria(s_antihorario, 2);
                
                if (
                	COORDS_PIEZA_PROVISORIA[X][3] 
                -	COORDS_PIEZA_PROVISORIA[X][PIVOTE] 
                +	COORDS_PIEZA_PROVISORIA[Y][3] 
                -	COORDS_PIEZA_PROVISORIA[Y][PIVOTE]
                ==	0
                )
                	if (s_antihorario)
                		COORDS_PIEZA_PROVISORIA[Y][3] += 2 * Math.pow(-1, 
                    		COORDS_PIEZA_PROVISORIA[Y][3] > COORDS_PIEZA_PROVISORIA[Y][PIVOTE] ?
	                        1 : 0
    	                );
                    else
                    	COORDS_PIEZA_PROVISORIA[X][3] += 2 * Math.pow(-1, 
                    		COORDS_PIEZA_PROVISORIA[X][3] > COORDS_PIEZA_PROVISORIA[X][PIVOTE] ?
	                        1 : 0
    	                );
                else
                	if (s_antihorario)
                		COORDS_PIEZA_PROVISORIA[X][3] += 2 * Math.pow(-1, 
                    		COORDS_PIEZA_PROVISORIA[X][3] > COORDS_PIEZA_PROVISORIA[X][PIVOTE] ?
	                        1 : 0
    	                );
                    else
                    	COORDS_PIEZA_PROVISORIA[Y][3] += 2 * Math.pow(-1, 
                    		COORDS_PIEZA_PROVISORIA[Y][3] > COORDS_PIEZA_PROVISORIA[Y][PIVOTE] ?
	                        1 : 0
    	                );
                break;
                
            case PIEZA_Z:
            case PIEZA_S:
            	esta_acostada = 
                	COORDS_PIEZA_ACTUAL[Y][1] 
                ==	COORDS_PIEZA_ACTUAL[Y][PIVOTE];
                
            	COORDS_PIEZA_PROVISORIA[X][1] += Math.pow(-1, 
                	esta_acostada 
                ^	pieza_actual_indice_color == 5
                ? 	1 : 0);
                COORDS_PIEZA_PROVISORIA[Y][1] += Math.pow(-1, esta_acostada ? 1 : 0);
                
                COORDS_PIEZA_PROVISORIA[X][2] += 2 * Math.pow(-1, 
                	esta_acostada 
                ^	pieza_actual_indice_color == 5
                ? 	1 : 0);
                
                COORDS_PIEZA_PROVISORIA[X][3] += 3 * Math.pow(-1, 
                	esta_acostada 
                ^	pieza_actual_indice_color == 5
                ? 	1 : 0);
                COORDS_PIEZA_PROVISORIA[Y][3] += Math.pow(-1, esta_acostada ? 1 : 0);
            
            	break;
                
            case PIEZA_I:
            	if(COORDS_PIEZA_PROVISORIA[Y][1] == COORDS_PIEZA_PROVISORIA[Y][PIVOTE])
                	for (int i = 1; i <= 3; i++) {
                    	COORDS_PIEZA_PROVISORIA[Y][i] = 
                        	COORDS_PIEZA_PROVISORIA[Y][PIVOTE]
                    	-	i
                      	+	(int)Math.pow(2, (i == 1 ? 1 : 0))
                      ;
                      COORDS_PIEZA_PROVISORIA[X][i] = COORDS_PIEZA_PROVISORIA[X][PIVOTE];
                	}
                else 
                	for (int i = 1; i <= 3; i++) {
                    	COORDS_PIEZA_PROVISORIA[X][i] = 
                        	COORDS_PIEZA_PROVISORIA[X][PIVOTE]
                    	-	i
                      	+	(int)Math.pow(2, (i == 1 ? 1 : 0))
                      ;
                      COORDS_PIEZA_PROVISORIA[Y][i] = COORDS_PIEZA_PROVISORIA[Y][PIVOTE];
                	}
            
            	break;
                
            // y para la pieza O ni me gasto porque no rota xd
        }
        
	// Código para evitar rotación a través de las paredes o de otras piezas
        for (int i = 0; i < 4; i++)
        	if (COORDS_PIEZA_PROVISORIA[X][i] >= WIDTH || COORDS_PIEZA_PROVISORIA[X][i] < 0
            ||	COORDS_PIEZA_PROVISORIA[Y][i] >= HEIGHT
	// Pero esta parte de acá da error cuando la pieza acaba de salir porque llama a un índice negativo
            ||	BOARD[COORDS_PIEZA_PROVISORIA[X][i]][COORDS_PIEZA_PROVISORIA[Y][i]] != Color.BLACK)
            	return;
        
        copiarAPiezaActual();
    }
    
    private void rotacionOrdinaria(boolean s_antihorario, int i) {
    	if (COORDS_PIEZA_PROVISORIA[X][i] == COORDS_PIEZA_PROVISORIA[X][PIVOTE]) {
        	COORDS_PIEZA_PROVISORIA[X][i] += Math.pow(-1, (
            	COORDS_PIEZA_PROVISORIA[Y][i] > COORDS_PIEZA_PROVISORIA[Y][PIVOTE] 
                ^ s_antihorario ? 
                1 : 0
            ));
            COORDS_PIEZA_PROVISORIA[Y][i] = COORDS_PIEZA_PROVISORIA[Y][PIVOTE];
        }
        else {
        	COORDS_PIEZA_PROVISORIA[Y][i] += Math.pow(-1, (
            	COORDS_PIEZA_PROVISORIA[X][i] < COORDS_PIEZA_PROVISORIA[X][PIVOTE] 
                ^ s_antihorario ? 
                1 : 0
            ));
            COORDS_PIEZA_PROVISORIA[X][i] = COORDS_PIEZA_PROVISORIA[X][PIVOTE];
        }
    }
/*    
    private Color getRandomColor() {
    	int indiceRojo = (int)(Math.random() * 255),
        	indiceVerde = (int)(Math.random() * 255),
            indiceAzul = (int)(Math.random() * 255);
            
        return new Color(indiceRojo, indiceVerde, indiceAzul);
    }
*/    
    public void paintComponent(Graphics g) {
    	super.paintComponent(g);
        boolean pieza_actual_tocada;
        
        for (int x = 0; x < WIDTH; x++) {
        	for (int y = 0; y < HEIGHT; y++) {
            	pieza_actual_tocada = false;
                for (int i = 0; i < 4; i++)
                	if (COORDS_PIEZA_ACTUAL[X][i] == x && COORDS_PIEZA_ACTUAL[Y][i] == y)
                    	pieza_actual_tocada = true;
                        
            	g.setColor(
                	pieza_actual_tocada && !modo_animacion ? 
                    	COLORS[pieza_actual_indice_color]
                    :	BOARD[x][y]);
                g.fillRect(BLOCK_SIZE*x, BLOCK_SIZE*y, BLOCK_SIZE, BLOCK_SIZE);
            }
        }
    }
}

public class MainFrame extends JFrame {
	public MainFrame() {
    	setLayout(new BorderLayout());
        add(new Board(), BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setTitle("Tetris");
        setResizable(false);
        setVisible(true);
    }
    
    public static void main(String[] args) {
    	SwingUtilities.invokeLater(() -> new MainFrame());
    }
}