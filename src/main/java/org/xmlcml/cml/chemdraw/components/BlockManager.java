/**
 * Copyright (C) 2001 Peter Murray-Rust (pm286@cam.ac.uk)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.xmlcml.cml.chemdraw.components;

import org.apache.log4j.Logger;
import org.xmlcml.cml.chemdraw.CDXConstants;


/**
 * @author P.Murray-Rust, 2001-2004
 **/


public class BlockManager implements CDXConstants {

    static Logger LOG = Logger.getLogger(BlockManager.class);


    private Block[] blocks;
    private int nBlocks = 0;
//    private Block currentBlock = null;
//    private int iBlock = 0;

    /**
     */
    public BlockManager() {
    }

    /**
     * @param bytes
     */
    public void setBytes(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return;
        }
        nBlocks = ((bytes.length - 1) / BLOCKSIZE) + 1;
        blocks = new Block[nBlocks];
        nBlocks = -1;
        int offset = 0;
        for (int i = 0; i < nBlocks; i++) {
            blocks[i] = new Block();
            blocks[nBlocks].addBytes(bytes, offset);
            offset += BLOCKSIZE;
        }
        try {
            prepareToRead();
        } catch (Exception e) {
            LOG.error("BUG "+e);
        }
    }

    private void prepareToRead() {
        if (nBlocks == 0) {
            throw new RuntimeException("No bytes to read");
        }
//        iBlock = 0;
//        currentBlock = blocks[0];
    }

//    private  byte getNextByte() throws CDXException {
//        byte b = 0;
//        try {
//            b = currentBlock.getNextByte();
//        } catch (CDXException e) {
//            if (++iBlock == nBlocks) {
//                throw new CDXException("no more bytes");
//            }
//            currentBlock = blocks[iBlock];
//            try {
//                b = currentBlock.getNextByte();
//            } catch (CDXException ee) {
//                LOG.error("BUG "+ee);
//            }
//        }
//        return b;
//    }
};

class Block implements CDXConstants {
    byte[] bytes;
    int currentByte = 0;

    /**
     */
    public Block() {
        ;
    }

    void addBytes(byte[] bb, int offset) {
        int bytesLeft = bb.length - offset;
        int bytesToCopy = (bytesLeft > BLOCKSIZE) ? BLOCKSIZE : bytesLeft;
        bytes = new byte[bytesToCopy];
        System.arraycopy(bb, offset, bytes, 0, bytesToCopy);
    }

//    private byte getNextByte() throws CDXException {
//        if (currentByte >= bytes.length) {
//            throw new CDXException("end of block");
//        }
//        return bytes[currentByte++];
//    }
}
