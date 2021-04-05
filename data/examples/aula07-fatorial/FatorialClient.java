import java.rmi.Naming;

public class FatorialClient {
	public static void main (String[] args) {
		int n;
		
		if	(args.length != 2)  {
			System.out.println("Uso: java FatorialClient <maquina> <numero>");
			System.exit(1);
		}
		try {
			FatorialInterface fatorial = (FatorialInterface) Naming.lookup("//" + args[0] + "/FatorialChuchu");
			n = fatorial.obtemFatorial(args[1]);
			System.out.println("Fatorial de " + args[1] + ": " + Integer.toString(n));
		} catch (Exception e) {
			System.out.println("NotasClient failed.");
			e.printStackTrace();
		}
	}
}
