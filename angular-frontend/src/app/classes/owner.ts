export class Owner {

    private _id?: number;
    private _name?: string;
    private _birthDateStr?: string;
    private _email?: string;
  
    // --- Getters ---
    public get id(): number | undefined {
      return this._id;
    }
  
    public get name(): string | undefined {
      return this._name;
    }
  
    public get birthDateStr(): string | undefined {
      return this._birthDateStr;
    }
  
    public get email(): string | undefined {
      return this._email;
    }
  
    // --- Setters ---
    public set id(value: number | undefined) {
      this._id = value;
    }
  
    public set name(value: string | undefined) {
      this._name = value;
    }
  
    public set birthDateStr(value: string | undefined) {
      this._birthDateStr = value;
    }
  
    public set email(value: string | undefined) {
      this._email = value;
    }
  }
  