package test.elsufoundation;

import java.util.*;

public class GuidGenerator {

	public static void main(String[] args) {
		// Creating a random UUID (Universally unique identifier).
		UUID uuid = UUID.randomUUID();
		String randomUUIDString = uuid.toString();

		System.out.println("Random UUID String = " + randomUUIDString);
		System.out.println("UUID version       = " + uuid.version());
		System.out.println("UUID variant       = " + uuid.variant());
	}
}
