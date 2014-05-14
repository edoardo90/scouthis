package it.poli.android.scoutthisme;
public enum ScoutMiniAppEnum {
	News,
	GPS, 
	Contapassi,
	Trovamici, 
	Lumus, 
	WalkieTalkie,
	WakeUp,
	WakeSet;

	@Override
	public String toString() {
		switch(this) {
			case WalkieTalkie: return "Walkie Talkie";
			case WakeSet : return "Alarm Settings";
			case WakeUp: return "Wake Up!";
			default: return this.name();
		}	
	}
}
