
import java.util.Scanner;



/**
 *
 * @author ramon
 */
public class Main {

    public static void main(String[] args) {
       
     Scanner scanner = new Scanner(System.in);
     
     int a , b , c ,troca , a1 , b1 ,c1 ;
     
     a = scanner.nextInt();
     b = scanner.nextInt();
     c = scanner.nextInt();
     a1 = a;
     b1 = b;
     c1 = c;
     
     
     if ( a > b ){
         troca = a;
         a = b ;
         b = troca;
         
     }if( a > c){
         troca = a;
         a = c;
         c = troca;
     }if( b > c){
         troca = b;
         b = c;
         c = troca;
     }
        System.out.println(+a);
        System.out.println(+b);
        System.out.println(+c);
        System.out.println("");
        System.out.println(+a1);
        System.out.println(+b1);
        System.out.println(+c1);
     
     
     }
             
             
        
        
    }

