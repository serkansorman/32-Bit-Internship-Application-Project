import java.util.ArrayList;
import java.util.Random;

/**
 * @author Serkan Sorman
 */

/**
 * 8x8 lik bir tahta üzerinde rastgele sayıda ve rastgele konumlara siyah pullar
 * yerleştirilir. Beyaz pul A1 konumundan harekete başlayıp en az bir siyah pulla
 * karşılaştırılarak H8 konumuna ulaştırılmaya çalışılır.
 */
public class Game {

    private Cell [][]board;
    private Random rand = new Random();
    private static final int SIZE = 8;
    /** Üzerinde siyah pul bulunan hücreleri tutar*/
    private ArrayList<Cell> stampCells;
    /**Beyaz pulun hareketi boyunca geçtiği hücreler tutulur*/
    private ArrayList<Cell> moves;

    /**
     * Tahta üzerindeki her bir hücrenin durumunu belirtir.
     */
    private enum CellType{
        EMPTY,
        STAMP,
        MARKED, // Gidilen yolu belirtir.
        UNMARKED,
        START,
        FINISH
    }

    /**
     * Tahta üzerindeki her bir hücrenin durumunu ve konumunu içerir
     */
    private class Cell{

        private CellType type;
        private int coordinateX;
        private int coordinateY;

        public Cell(CellType type,int x,int y){
            this.type = type;
            coordinateX = x;
            coordinateY = y;
        }

        private CellType getCellType(){
            return type;
        }

        private void setCellType(CellType type){
            this.type = type;
        }

        @Override
        public String toString() {
            char column = (char)('A' + coordinateY);
            return coordinateX + " " + column;
        }

        @Override
        public boolean equals(Object obj) {
            Cell c = (Cell) obj;
            return coordinateX == c.coordinateX && coordinateY == c.coordinateY && type == c.type;
        }
    }

    /**
     * Oyun tahtası hazırlanır.
     */
    public Game(){
        board = new Cell[SIZE][SIZE];
        stampCells = new ArrayList<>();
        moves = new ArrayList<>();

        //Başlangıçta tüm board boş hücrelerle doldurulur.
        for(int i=0; i<SIZE; ++i)
            for(int j=0; j<SIZE; ++j)
                board[i][j] = new Cell(CellType.EMPTY,i+1,j);

        // Board üzerindeki finish hücresi belirtilir
        board[SIZE - 1][SIZE - 1].setCellType(CellType.FINISH);

        putStamps();
        printBoard();

    }

    /**
     * Siyah pulları board üzerine yerleştirir.
     */
    private void putStamps(){
        // 0 ile 9 arasında random sayıda siyah pul üretilir.
        int numOfStamp = rand.nextInt(9);

       for(int i=0; i<numOfStamp; ++i){
            int row = rand.nextInt(SIZE - 1);
            int col = rand.nextInt(SIZE - 1);

            /*Siyah pul olan konuma ve START konumuna pul yerleştirilmez. Ayrıca START ve FİNİSH
            * hücrelerinin tamamen siyah pul ile çevrelenmesinden kaynaklı oyunun sonlanmama durumunu
            * engellemek için bir satır ve sütuna siyah pul yerleştirilmemiştir.*/
            if( !(row == 0 && col == 0) && row != SIZE - 1 && col != 0 && board[row][col].getCellType() == CellType.EMPTY) {
                board[row][col].setCellType(CellType.STAMP);
                stampCells.add(board[row][col]);
            }
        }
        /*En az bir siyah pul ile karşılaşılması için en kısa yol olan köşegen üzerinde
          random bir noktaya siyah bir pul yerleştirilir.*/
        int diagonalPosition = rand.nextInt(SIZE - 2)+1; // Start ve Finish noktası dahil edilmez
        if(board[diagonalPosition][diagonalPosition].getCellType() == CellType.EMPTY) {
            board[diagonalPosition][diagonalPosition].setCellType(CellType.STAMP);
            stampCells.add(board[diagonalPosition][diagonalPosition]);
        }
    }

    /**
     * Boardın güncel halini ekrana basar
     */
    public void printBoard(){

        System.out.println("Black Stamps: " +stampCells);
        System.out.println("Path: " +moves);

        Character column = 'A';
        for (int i = 0; i <SIZE; ++i)
            System.out.print(" " + (column++));
        System.out.println();

        for(int i=0; i<SIZE; ++i) {
            System.out.print(i + 1);
            for (int j = 0; j <SIZE; ++j) {
                if (board[i][j].getCellType() == CellType.EMPTY || board[i][j].getCellType() == CellType.UNMARKED)
                    System.out.print(". ");
                else if (board[i][j].getCellType() == CellType.MARKED)//Üzerinden geçilen hücre 'X' ile gösterilir
                    System.out.print("X ");
                else if (board[i][j].getCellType() ==  CellType.STAMP)//Stamplar 'O' ile gösterilir
                    System.out.print("O ");
                else if (board[i][j].getCellType() ==  CellType.START)
                    System.out.print("S ");
                else if (board[i][j].getCellType() ==  CellType.FINISH)
                    System.out.print("F ");
            }
            System.out.println();
        }
    }


    /**
     * @param i gezinilen hücrenin x konumu
     * @param j gezinilen hücrenin y konumu
     * A1 konumundan H8 konumuna siyah pullara çarpmadan gidilecek
     * mümkün olan en kısa yol recursive olarak bulunmaya çalışılır.
     * @return Finishe başarılı bir şekilde ulaşılırsa true return edilir.
     */
    public boolean findShortestPath(int i,int j){
        if(!isLegal(i,j))
            return false;
        if(board[i][j].getCellType() == CellType.FINISH) {
            board[0][0].setCellType(CellType.START);
            return true;
        }
        if(board[i][j].getCellType() != CellType.EMPTY)
            return false;
        board[i][j].setCellType(CellType.MARKED);

        // Sağ aşağı çapraza bak
        if(findShortestPath(i+1,j+1)){
            moves.add(board[i][j]);
            return true;
        }
        //Aşağı bak
        if(findShortestPath(i+1,j)){
            moves.add(board[i][j]);
            return true;
        }
        //Sağa bak
        if(findShortestPath(i,j+1)){
            moves.add(board[i][j]);
            return true;
        }
        //Yukarı bak
        if(findShortestPath(i-1,j)){
            moves.add(board[i][j]);
            return true;
        }
        //Sola bak
        if(findShortestPath(i,j-1)){
            moves.add(board[i][j]);
            return true;
        }

        //Çıkmaz yola girilmiş ise farklı bir yol denenmesi için işaretlenmiş cellin işareti kaldırılır.
        board[i][j].setCellType(CellType.UNMARKED);
        return false;
    }

    /**
     * Koordinatın geçerli olup olmadığını kontrol eder.
     * @param x
     * @param y
     * @return eger koordinat board üzerindeyse true return eder.
     */
    private  boolean isLegal(int x,int y){
        return x >= 0 && x < SIZE && y >= 0 && y < SIZE;
    }

}
