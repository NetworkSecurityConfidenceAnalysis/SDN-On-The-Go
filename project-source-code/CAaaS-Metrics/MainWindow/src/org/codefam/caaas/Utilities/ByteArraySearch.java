package org.codefam.caaas.Utilities;

public class ByteArraySearch {

    /**
     * Finds the first occurrence of the pattern in the text.
     */
    public int indexOf(byte[] data, byte[] pattern) {
        int[] failure = computeFailure(pattern);

        int j = 0;
        if (data.length == 0) {
            return -1;
        }

        for (int i = 0; i < data.length; i++) {
            while (j > 0 && pattern[j] != data[i]) {
                j = failure[j - 1];
            }
            if (pattern[j] == data[i]) {
                j++;
            }
            if (j == pattern.length) {
                return i - pattern.length + 1;
            }
        }
        // Ant to return -1, because but the string will probably never start at 0. -1 simplifies things
        return 0;
    }

    /**
     * Computes the failure function using a boot-strapping process, where the
     * pattern is matched against itself.
     */
    private int[] computeFailure(byte[] pattern) {
        int[] failure = new int[pattern.length];

        int j = 0;
        for (int i = 1; i < pattern.length; i++) {
            while (j > 0 && pattern[j] != pattern[i]) {
                j = failure[j - 1];
            }
            if (pattern[j] == pattern[i]) {
                j++;
            }
            failure[i] = j;
        }

        return failure;
    }

    static boolean matchData(byte[] srcData, byte[] dataToFind) {
        int iDataLen = srcData.length;
        int iDataToFindLen = dataToFind.length;
        boolean bGotData = false;
        int iMatchDataCntr = 0;
        for (int i = 0; i < iDataLen; i++) {
            if (srcData[i] == dataToFind[iMatchDataCntr]) {
                iMatchDataCntr++;
                bGotData = true;
            } else {
                if (srcData[i] == dataToFind[0]) {
                    iMatchDataCntr = 1;
                } else {
                    iMatchDataCntr = 0;
                    bGotData = false;
                }

            }

            if (iMatchDataCntr == iDataToFindLen) {
                return true;
            }
        }

        return false;
    }

    static public int SearchBytePattern(byte[] pattern, byte[] bytes) {
        int matches = 0;
        // precomputing this shaves some seconds from the loop execution
        int maxloop = bytes.length - pattern.length;
        for (int i = 0; i < maxloop; i++) {
            if (pattern[0] == bytes[i]) {
                boolean ismatch = true;
                for (int j = 1; j < pattern.length; j++) {
                    if (bytes[i + j] != pattern[j]) {
                        ismatch = false;
                        break;
                    }
                }
                if (ismatch) {
                    matches++;
                    i += pattern.length - 1;
                }
            }
        }
        return matches;
    }
}
