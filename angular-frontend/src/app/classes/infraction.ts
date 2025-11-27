export class Infraction {

    private _id?: number;
    private _infractionTimeStr?: string;
    private _vehicleRegistrationNumber?: string;
    private _vehicleSpeed?: number;
    private _radarId?: number;
    private _infractionAmount?: number;
  
    // --- Getters ---
    public get id(): number | undefined {
      return this._id;
    }
  
    public get infractionTimeStr(): string | undefined {
      return this._infractionTimeStr;
    }
  
    public get vehicleRegistrationNumber(): string | undefined {
      return this._vehicleRegistrationNumber;
    }
  
    public get vehicleSpeed(): number | undefined {
      return this._vehicleSpeed;
    }
  
    public get radarId(): number | undefined {
      return this._radarId;
    }
  
    public get infractionAmount(): number | undefined {
      return this._infractionAmount;
    }
  
    // --- Setters ---
    public set id(value: number | undefined) {
      this._id = value;
    }
  
    public set infractionTimeStr(value: string | undefined) {
      this._infractionTimeStr = value;
    }
  
    public set vehicleRegistrationNumber(value: string | undefined) {
      this._vehicleRegistrationNumber = value;
    }
  
    public set vehicleSpeed(value: number | undefined) {
      this._vehicleSpeed = value;
    }
  
    public set radarId(value: number | undefined) {
      this._radarId = value;
    }
  
    public set infractionAmount(value: number | undefined) {
      this._infractionAmount = value;
    }
  }
  