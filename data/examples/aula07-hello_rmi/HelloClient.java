import java.rmi.Naming;

class HelloClient {
	// Programa cliente para o exemplo "Hello, world!"
	public static void main (String[] argv) {
		try {
			HelloInterface hello = (HelloInterface) Naming.lookup ("//localhost/Hello");
			System.out.println (hello.say());
		} catch (Exception e) {
			System.out.println ("HelloClient failed:");
			e.printStackTrace();
		}
	}
}

