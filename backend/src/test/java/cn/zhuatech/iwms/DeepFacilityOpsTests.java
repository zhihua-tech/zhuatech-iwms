/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.iwms;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.regex.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.*;

/** 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
@SpringBootTest
@AutoConfigureMockMvc
class DeepFacilityOpsTests {
  @Autowired MockMvc mvc;

  /** 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void preventiveMaintenanceRunsThroughAcceptance() throws Exception {
    long a = asset("FAC-DEEP-1");
    admin("/api/admin/facility-ops/assets/" + a + "/activate", "{}");
    long p =
        id(
            post(
                    "/api/facility-ops/assets/" + a + "/plans",
                    "{\"planNo\":\"PM-1\",\"intervalDays\":30,\"checklist\":\"温度、振动、润滑检查\",\"nextDueDate\":\""
                        + date(1)
                        + "\"}")
                .andReturn());
    long w =
        id(
            post(
                    "/api/facility-ops/plans/" + p + "/work-orders",
                    "{\"workOrderNo\":\"WO-1\",\"priority\":\"P2\",\"dueDate\":\""
                        + date(2)
                        + "\"}")
                .andReturn());
    post("/api/facility-ops/work-orders/" + w + "/assign", "{\"assignee\":\"工程师甲\"}");
    post("/api/facility-ops/work-orders/" + w + "/start", "{}");
    post(
            "/api/facility-ops/work-orders/" + w + "/complete",
            "{\"meterReading\":1280,\"note\":\"保养完成，运行正常\"}")
        .andExpect(jsonPath("$.data.status").value("WAITING_ACCEPTANCE"));
    admin(
            "/api/admin/facility-ops/work-orders/" + w + "/accept",
            "{\"accepted\":true,\"note\":\"现场验收通过\"}")
        .andExpect(jsonPath("$.data.status").value("CLOSED"));
  }

  /** 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void inactiveAssetAndMeterRollbackAreBlocked() throws Exception {
    long a = asset("FAC-DEEP-2");
    post(
            "/api/facility-ops/assets/" + a + "/plans",
            "{\"planNo\":\"PM-X\",\"intervalDays\":30,\"checklist\":\"检查\",\"nextDueDate\":\""
                + date(1)
                + "\"}")
        .andExpect(status().isConflict());
    admin("/api/admin/facility-ops/assets/" + a + "/activate", "{}");
    long p =
        id(
            post(
                    "/api/facility-ops/assets/" + a + "/plans",
                    "{\"planNo\":\"PM-2\",\"intervalDays\":30,\"checklist\":\"检查\",\"nextDueDate\":\""
                        + date(1)
                        + "\"}")
                .andReturn());
    long w =
        id(
            post(
                    "/api/facility-ops/plans/" + p + "/work-orders",
                    "{\"workOrderNo\":\"WO-2\",\"priority\":\"P2\",\"dueDate\":\""
                        + date(2)
                        + "\"}")
                .andReturn());
    post("/api/facility-ops/work-orders/" + w + "/assign", "{\"assignee\":\"工程师乙\"}");
    post("/api/facility-ops/work-orders/" + w + "/start", "{}");
    post(
        "/api/facility-ops/work-orders/" + w + "/complete",
        "{\"meterReading\":100,\"note\":\"首次完工\"}");
    admin(
        "/api/admin/facility-ops/work-orders/" + w + "/accept",
        "{\"accepted\":true,\"note\":\"通过\"}");
    long w2 =
        id(
            post(
                    "/api/facility-ops/plans/" + p + "/work-orders",
                    "{\"workOrderNo\":\"WO-3\",\"priority\":\"P3\",\"dueDate\":\""
                        + date(32)
                        + "\"}")
                .andReturn());
    post("/api/facility-ops/work-orders/" + w2 + "/assign", "{\"assignee\":\"工程师乙\"}");
    post("/api/facility-ops/work-orders/" + w2 + "/start", "{}");
    post(
            "/api/facility-ops/work-orders/" + w2 + "/complete",
            "{\"meterReading\":90,\"note\":\"异常回退\"}")
        .andExpect(status().isConflict());
  }

  /** 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void serviceSlaEscalationAndDuplicateAssetWork() throws Exception {
    asset("FAC-DEEP-3");
    post("/api/facility-ops/assets", assetBody("FAC-DEEP-3")).andExpect(status().isConflict());
    post(
        "/api/facility-ops/service-requests",
        "{\"organizationCode\":\"ZH-SH\",\"requestNo\":\"SR-1\",\"category\":\"空调故障\",\"priority\":\"P1\",\"description\":\"机房温度升高\",\"location\":\"A座3层\"}");
    mvc.perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post(
                    "/api/facility-ops/sla/escalate")
                .param("organizationCode", "ZH-SH")
                .param("asOf", java.time.LocalDateTime.now().plusDays(1).toString())
                .with(httpBasic("operator", "operator123")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").value(1));
  }

  /** 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  long asset(String n) throws Exception {
    return id(post("/api/facility-ops/assets", assetBody(n)).andReturn());
  }

  /** 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  String assetBody(String n) {
    return "{\"organizationCode\":\"ZH-SH\",\"assetNo\":\""
        + n
        + "\",\"name\":\"冷水机组\",\"location\":\"A座B1\",\"criticality\":\"CRITICAL\",\"warrantyEnd\":\""
        + date(730)
        + "\"}";
  }

  /** 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  ResultActions post(String u, String b) throws Exception {
    return mvc.perform(
        org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post(u)
            .with(httpBasic("operator", "operator123"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(b));
  }

  /** 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  ResultActions admin(String u, String b) throws Exception {
    return mvc.perform(
        org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post(u)
            .with(httpBasic("admin", "admin123"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(b));
  }

  /** 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */

  /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  String date(int offset) {
    return java.time.LocalDate.now().plusDays(offset).toString();
  }

  long id(MvcResult r) throws Exception {
    var m = Pattern.compile("\"id\":(\\d+)").matcher(r.getResponse().getContentAsString());
    Assertions.assertTrue(m.find());
    return Long.parseLong(m.group(1));
  }
}
