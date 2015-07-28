/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\Users\\Mukesh\\workspace\\GpsAndSensors\\src\\com\\example\\gpsandsensors\\Sensor_aidl.aidl
 */
package com.example.gpsandsensors;
public interface Sensor_aidl extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.example.gpsandsensors.Sensor_aidl
{
private static final java.lang.String DESCRIPTOR = "com.example.gpsandsensors.Sensor_aidl";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.example.gpsandsensors.Sensor_aidl interface,
 * generating a proxy if needed.
 */
public static com.example.gpsandsensors.Sensor_aidl asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.example.gpsandsensors.Sensor_aidl))) {
return ((com.example.gpsandsensors.Sensor_aidl)iin);
}
return new com.example.gpsandsensors.Sensor_aidl.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_send_Location:
{
data.enforceInterface(DESCRIPTOR);
android.location.Location _result = this.send_Location();
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_distance:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
float _result = this.distance(_arg0);
reply.writeNoException();
reply.writeFloat(_result);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.example.gpsandsensors.Sensor_aidl
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public android.location.Location send_Location() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.location.Location _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_send_Location, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = android.location.Location.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public float distance(int steps) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
float _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(steps);
mRemote.transact(Stub.TRANSACTION_distance, _data, _reply, 0);
_reply.readException();
_result = _reply.readFloat();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_send_Location = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_distance = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
}
public android.location.Location send_Location() throws android.os.RemoteException;
public float distance(int steps) throws android.os.RemoteException;
}
