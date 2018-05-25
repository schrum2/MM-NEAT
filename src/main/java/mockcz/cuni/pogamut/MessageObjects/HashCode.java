package mockcz.cuni.pogamut.MessageObjects;

/**
 * This class is meant for counting hash codes from any possible type.
 * It declares a two heavily overloaded methods ;-).
 * 
 * 1) hash(whatever) -> returns hash number for 'whatever' (of whatever type)
 * 2) add(whatever)  -> add number to the hashCode for 'whatever' (of whatever type)
 * 
 * Typical usage: 
 * 
 * Usualy you will create a method private countHashCode() which you will call from
 * within the constructors after you've initialized variables from which you want
 * to count the hash code. It will look like this:
 * 
 * private int getHashCode(){
 * 	 HashCode hc = new HashCode(); // creating new HashCode instance
 * 	 hc.add(myFirstIntValue);      // adding first parametr to hash code
 *   hc.add(mySecondIntValue);     // second...
 *   hc.add(myFloatValue);         // third...
 *   return hc.getHash();          // returning the hash
 * }
 * 
 * private final int hashCode;
 * 
 * public int ConstrucotrOfMyClass(){
 *   // initializing variables		
 *   hashCode = getHashCode();
 * }
 * 
 * public int hashCode(){
 * 	 return hashCode;
 * }
 * 
 */
public final class HashCode {
	
	public int hash(boolean b){
		return b ? 0 : 1;
	}
	
	public int hash(byte b){
		return (int) b;
	}
	
	public int hash(char c){
		return (int) c;
	}
	
	public int hash(short s){
		return (int) s;
	}
	
	public int hash(int i){
		return (int) i;
	}
	
	public int hash(long i){
		return (int) i;
	}
	
	public int hash(float f){
		return Float.floatToIntBits(f);
	}
	
	public int hash(double d){		
		long l = Double.doubleToLongBits(d);
		return (int)(l ^ (l >>> 32));
	}
	
	public int hash(Object o){
		return  o == null ? 0 : o.hashCode();
	}
	
	private int hashCode;
	
	public HashCode(){
		hashCode = 17;
	}
		
	private void addNumber(int number){
		hashCode = 37 * hashCode + number;
	}
	
	public void add(boolean b){
		addNumber(hash(b));
	}
	
	public void add(byte b){
		addNumber(hash(b));
	}
	
	public void add(char c){
		addNumber(hash(c));
	}
	
	public void add(short s){
		addNumber(hash(s));
	}
	
	public void add(int i){
		addNumber(hash(i));
	}
	
	public void add(long l){
		addNumber(hash(l));
	}
	
	public void add(float f){
		addNumber(hash(f));
	}
	
	public void add(double d){
		addNumber(hash(d));
	}
	
	public void add(Object o){
		addNumber(hash(o));
	}
	
	public int getHash(){
		return hashCode;
	}

}
