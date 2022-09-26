package dev.vality.wb.list.manager.resource;

import dev.vality.damsel.wb_list.WbListServiceSrv;
import dev.vality.woody.thrift.impl.http.THServiceBuilder;
import lombok.RequiredArgsConstructor;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;

@WebServlet("/wb_list/v1")
@RequiredArgsConstructor
public class WbListServlet extends GenericServlet {

    private final WbListServiceSrv.Iface wbListHandler;
    private Servlet thriftServlet;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        thriftServlet = new THServiceBuilder()
                .build(WbListServiceSrv.Iface.class, wbListHandler);
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        thriftServlet.service(req, res);
    }
}
