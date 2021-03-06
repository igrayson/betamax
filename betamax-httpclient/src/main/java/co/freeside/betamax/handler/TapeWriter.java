/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package co.freeside.betamax.handler;

import java.util.logging.*;
import co.freeside.betamax.*;
import co.freeside.betamax.message.*;
import co.freeside.betamax.tape.*;
import static co.freeside.betamax.Headers.*;
import static java.util.logging.Level.*;

public class TapeWriter extends ChainedHttpHandler {
    public TapeWriter(Recorder recorder) {
        this.recorder = recorder;
    }

    public Response handle(Request request) {
        Tape tape = recorder.getTape();

        if (tape == null) {
            throw new NoTapeException();
        }

        if (!tape.isWritable()) {
            throw new NonWritableTapeException();
        }

        Response response = chain(request);
        LOG.log(INFO, "Recording to '" + tape.getName() + "'");
        tape.record(request, response);
        response.addHeader(X_BETAMAX, "REC");

        return response;
    }

    private final Recorder recorder;
    private static final Logger LOG = Logger.getLogger(TapeWriter.class.getName());
}
