/* 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.iwms;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

/**
 * 失效测试日期不能通过删除生产校验掩盖。上海如静知华信息科技有限公司 https://www.zhuatech.cn/
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@SpringBootTest
@AutoConfigureMockMvc
class MaintenanceDateBoundaryTest {
  @Autowired MockMvc mvc;
  private final JsonMapper json = new JsonMapper();

  /** 上海如静知华信息科技有限公司 https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
  @Test
  void pastDueOrderRejectedAndFutureOrderAccepted() throws Exception {
    String suffix = UUID.randomUUID().toString();
    var asset =
        mvc.perform(
                post("/api/facility-ops/assets")
                    .with(httpBasic("operator", "operator123"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        json.writeValueAsString(
                            Map.of(
                                "organizationCode",
                                "ZH-SH",
                                "assetNo",
                                "DATE-" + suffix,
                                "name",
                                "日期回归设备",
                                "location",
                                "测试区",
                                "criticality",
                                "NORMAL"))))
            .andExpect(status().isOk())
            .andReturn();
    long assetId = json.readTree(asset.getResponse().getContentAsString()).at("/data/id").asLong();
    mvc.perform(
            post("/api/admin/facility-ops/assets/" + assetId + "/activate")
                .with(httpBasic("admin", "admin123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
        .andExpect(status().isOk());
    var plan =
        mvc.perform(
                post("/api/facility-ops/assets/" + assetId + "/plans")
                    .with(httpBasic("operator", "operator123"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        json.writeValueAsString(
                            Map.of(
                                "planNo",
                                "DATE-PM-" + suffix,
                                "intervalDays",
                                30,
                                "checklist",
                                "日期边界检查",
                                "nextDueDate",
                                LocalDate.now().plusDays(1).toString()))))
            .andExpect(status().isOk())
            .andReturn();
    long planId = json.readTree(plan.getResponse().getContentAsString()).at("/data/id").asLong();
    mvc.perform(
            post("/api/facility-ops/plans/" + planId + "/work-orders")
                .with(httpBasic("operator", "operator123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    json.writeValueAsString(
                        Map.of(
                            "workOrderNo",
                            "DATE-WO-" + suffix,
                            "priority",
                            "P2",
                            "dueDate",
                            LocalDate.now().minusDays(1).toString()))))
        .andExpect(status().isBadRequest());
    mvc.perform(
            post("/api/facility-ops/plans/" + planId + "/work-orders")
                .with(httpBasic("operator", "operator123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    json.writeValueAsString(
                        Map.of(
                            "workOrderNo",
                            "DATE-WO-" + suffix,
                            "priority",
                            "P2",
                            "dueDate",
                            LocalDate.now().plusDays(2).toString()))))
        .andExpect(status().isOk());
  }
}
