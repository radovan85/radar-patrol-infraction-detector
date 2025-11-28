export class Radar {

    private _id?: number;
    private _name?: string;
    private _status?: number;
    private _maxSpeed?: number;
    private _longitude?: number;
    private _latitude?: number;
  
    // --- Getters ---
    public get id(): number | undefined {
      return this._id;
    }
  
    public get name(): string | undefined {
      return this._name;
    }
  
    public get status(): number | undefined {
      return this._status;
    }
  
    public get maxSpeed(): number | undefined {
      return this._maxSpeed;
    }
  
    public get longitude(): number | undefined {
      return this._longitude;
    }
  
    public get latitude(): number | undefined {
      return this._latitude;
    }
  
    // --- Setters ---
    public set id(value: number | undefined) {
      this._id = value;
    }
  
    public set name(value: string | undefined) {
      this._name = value;
    }
  
    public set status(value: number | undefined) {
      this._status = value;
    }
  
    public set maxSpeed(value: number | undefined) {
      this._maxSpeed = value;
    }
  
    public set longitude(value: number | undefined) {
      this._longitude = value;
    }
  
    public set latitude(value: number | undefined) {
      this._latitude = value;
    }
  }
  