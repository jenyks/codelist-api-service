package com.pagero.services.codelist.restapi.mappers

import com.google.protobuf2.ByteString
import com.pagero.services.staticmapping.spec.CodelistSaveRequest
import com.pagero.services.codelist.model.CodelistInfo

object CodeListInfoMapper {
  def apply(codelistInfo: CodelistInfo): CodelistSaveRequest = {
    CodelistSaveRequest(Option(codelistInfo.codelistName), Option(codelistInfo.codelistDescription),
      codelistInfo.companies.toList, Option(ByteString.copyFrom(new String(java.util.Base64.getDecoder.decode(codelistInfo.payload)).getBytes("UTF-8"))))
  }
}
