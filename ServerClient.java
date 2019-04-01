import java.io.*; 
import java.text.*; 
import java.util.*; 
import java.net.*; 

public class ServerClient{
	
	public static void main(String[] args) 
	throws IOException{

	try{

		Scanner scn = new Scanner(System.in);

		System.out.println("Select mode :- Client | Server");
		String choice = scn.next();

		if(choice.equals("Server")){

			// server is listening on port 5056 
			ServerSocket ss = new ServerSocket(5056); 

			//run infinite loop to get client request
			while(true){

				Socket s = null;

				try{

					// socket object to receive incoming client requests 
					s = ss.accept();

					System.out.println("A new client is connected : " + s); 
					  
					// obtaining input and out streams 
					DataInputStream dis = new DataInputStream(s.getInputStream()); 
					DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 
					  
					System.out.println("Assigning new thread for this client"); 
	  
					// create a new thread object 
					Thread t = new ClientHandler(s, dis, dos); 
	  
					// Invoking the start() method 
					t.start(); 

				} catch (Exception e){ 
					s.close(); 
					e.printStackTrace(); 
					System.out.println(e.getMessage());
				} 

			}

		} else if(choice.equals("Client")){

			try{  
				// getting localhost ip 
				InetAddress ip = InetAddress.getByName("localhost"); 
		  
				// establish the connection with server port 5056 
				Socket s = new Socket(ip, 5056); 
		  
				// obtaining input and out streams 
				DataInputStream dis = new DataInputStream(s.getInputStream()); 
				DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 
		  
				// the following loop performs the exchange of 
				// information between client and client handler 
				while (true)  
				{ 
					//System.out.println(dis.readUTF()); 
					String tosend = scn.nextLine(); 
					dos.writeUTF(tosend); 
					  
					// If client sends exit,close this connection  
					// and then break from the while loop 
					if(tosend.equals("Exit")) 
					{ 
						System.out.println("Closing this connection : " + s); 
						s.close(); 
						System.out.println("Connection closed"); 
						break; 
					} 
					  
					// printing date or time as requested by client 
					String received = dis.readUTF(); 
					System.out.println(received); 
				} 
				  
				// closing resources 
				scn.close(); 
				dis.close(); 
				dos.close(); 
			}catch(Exception e){ 
				e.printStackTrace(); 
			} 
		} 

	} catch (Exception e){
		System.out.println(e.getMessage());
	}

	}
}


//ClientHandler class 
class ClientHandler extends Thread  
{ 
 DateFormat fordate = new SimpleDateFormat("yyyy/MM/dd"); 
 DateFormat fortime = new SimpleDateFormat("hh:mm:ss"); 
 final DataInputStream dis; 
 final DataOutputStream dos; 
 final Socket s; 
   

 // Constructor 
 public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos)  
 { 
     this.s = s; 
     this.dis = dis; 
     this.dos = dos; 
 } 

 @Override
 public void run()  
 { 
     String received; 
     String toreturn; 
     while (true)  
     { 
         try { 

             // Ask user what he wants 
             dos.writeUTF("What do you want?[Date | Time]..\n"+ 
                         "Type Exit to terminate connection."); 
               
             // receive the answer from client 
             received = dis.readUTF(); 
               
             if(received.equals("Exit")) 
             {  
                 System.out.println("Client " + this.s + " sends exit..."); 
                 System.out.println("Closing this connection."); 
                 this.s.close(); 
                 System.out.println("Connection closed"); 
                 break; 
             } 
               
             // creating Date object 
             Date date = new Date(); 
               
             // write on output stream based on the 
             // answer from the client 
             switch (received) { 
               
                 case "Date" : 
                     toreturn = fordate.format(date); 
                     dos.writeUTF(toreturn); 
                     break; 
                       
                 case "Time" : 
                     toreturn = fortime.format(date); 
                     dos.writeUTF(toreturn); 
                     break; 
                       
                 default: 
                     dos.writeUTF("Invalid input"); 
                     break; 
             } 
         } catch (IOException e) { 
             e.printStackTrace(); 
         } 
     } 
       
     try
     { 
         // closing resources 
         this.dis.close(); 
         this.dos.close(); 
           
     }catch(IOException e){ 
         e.printStackTrace(); 
     } 
 } 
} 
