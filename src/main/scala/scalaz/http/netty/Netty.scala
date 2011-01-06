package scalaz
package http
package netty

import request.{Lines, Methods, RequestHeaders, Uris}
import response.{ResponseHeaders, Bodys}
import response.xhtml.Doctypes

object Netty extends EntityHeaders
        with GeneralHeaders
        with Versions
        with Lines
        with Methods
        with RequestHeaders
        with ResponseHeaders
        with Uris
        with StreamStreamApplications
        with Bodys
        with Doctypes
        with HttpNettyRequests
        with HttpNettyResponses