/*
Copyright (c) 2010 Safe Creative (http://www.safecreative.org)

Permission is hereby granted, free of charge, to any person
obtaining a copy of this software and associated documentation
files (the "Software"), to deal in the Software without
restriction, including without limitation the rights to use,
copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the
Software is furnished to do so, subject to the following
conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.
 */
package org.safecreative.api.wrapper;

/**
 * Enumeration of available search methods: fields,hashes
 *
 * @author mpolo@safecreative.org
 * @author jguillo@safecreative.org
 */
public enum SearchMethod {
    //Field methods: (Using searchWorksByFields)
    USER_CODE       ("user.code"),
    USER_NAME       ("user.name"),
    WORK_CODE       ("code"),
    WORK_TITLE      ("name"),
    WORK_EXCERPT    ("excerpt"),
    WORK_NOTES      ("obs"),
    WORK_TAGS       ("tag"),
    WORK_TYPE       ("workType.code"),
    WORK_TYPE_NAME  ("workType.name"),
    WORK_TYPE_GROUP ("workTypeGroup.code"),
    WORK_TYPE_GROUP_NAME  ("workTypeGroup.name"),
    WORK_PROP_MIME  ("workproperty.sc.mimetype"),
    WORK_PROP_TIME  ("workproperty.sc.lastmodified"),

    LICENSE_CODE    ("license.code"),
    LICENSE_NAME    ("license.name"),
    LICENSE_SHORT_NAME    ("license.shortName"),
    DOWNLOADABLE    ("allowDownload"),

    //Field methods: (Using searchWorksByContent)
    /**
     * Adler32 checksum of file contents (16 hex digits- first 8 digits should be zero)
     */
    WORK_CNT_ADLER32 ("adler32"),
    /**
     * CRC32 checksum of file contents (16 hex digits – first 8 digits should be zero)
     */
    WORK_CNT_CRC32   ("crc32"),  
    /**
     * File hash for ed2k network (32 hex digits)
     */
    WORK_CNT_ED2K   ("ed2kfileid"),   
    /**
     * SHA1 hash of the first 32k of file contents (40 hex digits)
     */
    WORK_CNT_BEGIN  ("part32k.first"),      
    /**
     * SHA1 hash of the middle 32k bytes of file contents (40 hex digits)
     * It is calculated as the SHA1 of a 32k chunk starting on file position (filesize  – 32k) / 2.
     * If the file size is less than 32k, it is the hash of the whole file.
     */
    WORK_CNT_MIDDLE ("part32k.middle"),  
    /**
     * SHA1 hash of the last 32k bytes of file contents (40 hex digits)
     */
    WORK_CNT_END    ("part32k.last"),          
    /**
     * MD2 hash of file contents (32 hex digits)
     */
    WORK_CNT_MD2   ("md2"),
    /**
     * MD4 hash of file contents (32 hex digits)
     */
    WORK_CNT_MD4   ("md4"),
    /**
     * MD5 hash of file contents (32 hex digits)
     */
    WORK_CNT_MD5   ("md5"),
    /**
     * SHA-1 hash of file contents (32 hex digits)
     */
    WORK_CNT_SHA1  ("sha1"),
    /**
     * SHA-384 hash of file contents (96 hex digits)
     */
    WORK_CNT_SHA384  ("sha384"),
    /**
     * SHA-512 hash of file contents (128 hex digits)
     */
    WORK_CNT_SHA512  ("sha512"),

    // TORRENT_*: SHA1 hash of torrent chunk hashes for different chunk sizes (40 hex digits)
    /**
     * SHA1 hash of 32K torrent chunk (40 hex digits)
     */
    WORK_CNT_TORRENT_32K  ("torrent.32768"),
    /**
     * SHA1 hash of 48K torrent chunk (40 hex digits)
     */
    WORK_CNT_TORRENT_48K  ("torrent.49152"),
    /**
     * SHA1 hash of 64K torrent chunk (40 hex digits)
     */
    WORK_CNT_TORRENT_64K  ("torrent.65536"),
    /**
     * SHA1 hash of 128K torrent chunk (40 hex digits)
     */
    WORK_CNT_TORRENT_128K  ("torrent.131072"),
    /**
     * SHA1 hash of 192K torrent chunk (40 hex digits)
     */
    WORK_CNT_TORRENT_192K  ("torrent.196608"),
    /**
     * SHA1 hash of 256K torrent chunk (40 hex digits)
     */
    WORK_CNT_TORRENT_256K  ("torrent.262144"),
    /**
     * SHA1 hash of 384K torrent chunk (40 hex digits)
     */
    WORK_CNT_TORRENT_384K  ("torrent.393216"),
    /**
     * SHA1 hash of 512K torrent chunk (40 hex digits)
     */
    WORK_CNT_TORRENT_512K  ("torrent.524288"),
    /**
     * SHA1 hash of 768K torrent chunk (40 hex digits)
     */
    WORK_CNT_TORRENT_768K  ("torrent.786432"),
    /**
     * SHA1 hash of 1024K torrent chunk (40 hex digits)
     */
    WORK_CNT_TORRENT_1024K  ("torrent.1048576"),
    /**
     * SHA1 hash of 1536K torrent chunk (40 hex digits)
     */
    WORK_CNT_TORRENT_1536K  ("torrent.1572864"),
    /**
     * SHA1 hash of 2048K torrent chunk (40 hex digits)
     */
    WORK_CNT_TORRENT_2048K  ("torrent.2097152"),
    /**
     * SHA1 hash of 3072K torrent chunk (40 hex digits)
     */
    WORK_CNT_TORRENT_3072K  ("torrent.3145728"),
    /**
     * SHA1 hash of 4096K torrent chunk (40 hex digits)
     */
    WORK_CNT_TORRENT_4096K  ("torrent.4194304"),


    //Hash methods: (Using searchWorksByHash)
    HASH_MD5        ("md5"),
    HASH_SHA1       ("sha1")
    ;


    private String fieldName;

    private SearchMethod(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * Get this field name
     * @return
     */
    public String getFieldName() {
        return fieldName;
    }

    @Override
    public String toString() {
        return getFieldName();
    }
}
