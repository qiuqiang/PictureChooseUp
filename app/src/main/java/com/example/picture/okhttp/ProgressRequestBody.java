package com.example.picture.okhttp;

import android.util.Log;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/*Created by 邱强 on 2019/1/16.
 * E-Mail 2536555456@qq.com
 */
public class ProgressRequestBody extends RequestBody {
    private RequestBody requestBody;
    private BufferedSink bufferedSink;


    public ProgressRequestBody(RequestBody body) {
        requestBody = body;
    }

    @Override
    public MediaType contentType() {
        return requestBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return requestBody.contentLength();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        if (bufferedSink == null) {
            bufferedSink = Okio.buffer(sink(sink));
        }
        //写入
        requestBody.writeTo(bufferedSink);
        //刷新
        bufferedSink.flush();
    }

    private Sink sink(BufferedSink sink) {
        return new ForwardingSink(sink) {
            long bytesWritten = 0L;
            long contentLength = 0L;

            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                if (contentLength == 0) {
                    contentLength = contentLength();
                }
                bytesWritten += byteCount;
                //回调
                double bili = bytesWritten * 100.0 / contentLength * 100.0;
                int progress = (int) (bili / 100);
                if (onFileUpClick != null) {
                    onFileUpClick.fileUpLoading(progress);
                }
            }
        };
    }

    private OnFileUpClick onFileUpClick;

    public void setOnFileUpClick(OnFileUpClick onFileUpClick) {
        this.onFileUpClick = onFileUpClick;
    }

    public interface OnFileUpClick {
        void fileUpLoading(int progress);
    }
}
