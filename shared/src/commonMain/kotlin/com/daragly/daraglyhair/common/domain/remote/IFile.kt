package com.daragly.daraglyhair.common.domain.remote

import com.daragly.daraglyhair.common.data.repository.remote.File

interface IFile {
    val name: String
    val value: ByteArray
    val mimeType: File.MimeTypeInfo
    val size: Int
}
