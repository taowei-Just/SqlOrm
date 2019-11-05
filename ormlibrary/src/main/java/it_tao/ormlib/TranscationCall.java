package it_tao.ormlib;

public interface TranscationCall {
    void onCompleted();

    void onError(Exception e);
}
