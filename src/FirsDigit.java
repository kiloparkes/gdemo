
public class FirsDigit {

	public static void main(String[] args) {
		
		String s = args[0];
		long number = Long.parseLong(s);
		int firstDigit = Integer.parseInt(Long.toString(number).substring(0, 1));

		System.out.println(firstDigit);
	}
}
