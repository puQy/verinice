package sernet.gs.reveng.importData;

/**
 * Various constants from tables used in the GSTOOL database.
 * 
 * @author koderman
 *
 */
public class GSDBConstants {
	public static byte USERDEF_YES = 2;
	public static byte USERDEF_NO = 1;
	
	public static byte YESNO_YES = 1;
	public static byte YESNO_NO = 2;
	
	public static byte UNJ_UNBEARBEITET =1;
	public static byte UNJ_NEIN=2;
	public static byte UNJ_JA=3;
	
	public static byte GEFAEHRSKAT_ELEMENTARE_GEFAEHRDUNG =0;
	public static byte GEFAEHRSKAT_HOEHERE_GEWALT=1;
	public static byte GEFAEHRSKAT_ORG_MAENGEL=2;
	public static byte GEFAEHRSKAT_MENSCHLICHE_FEHLHANDLUNG=3;
	public static byte GEFAEHRSKAT_TECHNISCHES_VERSAGEN=4;
	public static byte GEFAEHRSKAT_VORSAETZLICHE_HANDLUNG=5;
	public static byte GEFAEHRSKAT_DATENSCHUTZ=6;
	
	public static byte RABEHAND_UNBEARBEITET=1;
	public static byte RABEHAND_A=2;
	public static byte RABEHAND_B=3;
	public static byte RABEHAND_C=4;
	public static byte RABEHAND_D=5;
	
	public static char RA_BEHAND_UNBEARBEITET='-';
	public static char RA_BEHAND_A_REDUKTION ='A';
	public static char RA_BEHAND_B_UMSTRUKTURIERUNG ='B';
	public static char RA_BEHAND_C_UEBERNAHME ='C';
	public static char RA_BEHAND_D_TRANSFER ='D';
}
