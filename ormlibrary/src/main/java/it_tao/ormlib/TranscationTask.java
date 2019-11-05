package it_tao.ormlib;

public abstract class TranscationTask implements Runnable {
    DB2 db;
    TranscationCall transcationCall;

    public TranscationTask(DB2 db, TranscationCall transcationCall) {
        this.db = db;
        this.transcationCall = transcationCall;
    }

    @Override
    public void run() {
        try {
            db.beginTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            transcationCall.onError(e);
            return;
        }
        try {
            taskRun(db);
            try {
                db.commitTransaction();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (null!=transcationCall)
                transcationCall.onCompleted();
        } catch (Exception e) {
            e.printStackTrace();
            transcationCall.onError(e);
        } finally {
            try {
                db.commitTransaction();
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }

    protected abstract void taskRun(DB2 db) throws Exception;
}
