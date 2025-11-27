export class Vehicle {

  private _id?: number;
  private _registrationNumber?: string;
  private _brand?: string;
  private _fiscalPower?: number;
  private _model?: string;
  private _manufactureYear?: number;
  private _ownerId?: number;

  // --- Getters ---
  public get id(): number | undefined {
    return this._id;
  }

  public get registrationNumber(): string | undefined {
    return this._registrationNumber;
  }

  public get brand(): string | undefined {
    return this._brand;
  }

  public get fiscalPower(): number | undefined {
    return this._fiscalPower;
  }

  public get model(): string | undefined {
    return this._model;
  }

  public get ownerId(): number | undefined {
    return this._ownerId;
  }

  // --- Setters ---
  public set id(value: number | undefined) {
    this._id = value;
  }

  public set registrationNumber(value: string | undefined) {
    this._registrationNumber = value;
  }

  public set brand(value: string | undefined) {
    this._brand = value;
  }

  public set fiscalPower(value: number | undefined) {
    this._fiscalPower = value;
  }

  public set model(value: string | undefined) {
    this._model = value;
  }

  public set ownerId(value: number | undefined) {
    this._ownerId = value;
  }

  public get manufactureYear(): number | undefined {
    return this._manufactureYear;
  }

  public set manufactureYear(value: number | undefined) {
    this._manufactureYear = value;
  }


}
