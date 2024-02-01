/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.csp.sentinel.dashboard.rule.nacos.gateway;

import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.gateway.GatewayFlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRuleProvider;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.NacosConfigUtil;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.nacos.api.config.ConfigService;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eric Zhao
 * @since 1.4.0
 */
@Component("gatewayFlowRuleNacosProvider")
public class GatewayFlowRuleNacosProvider implements DynamicRuleProvider<List<GatewayFlowRule>> {

    @Autowired
    private ConfigService configService;
    @Autowired
    private Converter<String, List<GatewayFlowRule>> converter;

    @Override
    public List<GatewayFlowRule> getRules(String appName) throws Exception {

        // 从nacos中拉取GatewayFlowRule集合
        String rules = configService.getConfig(appName + NacosConfigUtil.GATEWAY_FLOW_DATA_ID_POSTFIX, NacosConfigUtil.GROUP_ID, 3000);
        if (StringUtil.isEmpty(rules)) {
            return new ArrayList<>();
        }
        return converter.convert(rules);

    }

    public List<GatewayFlowRuleEntity> getRules(String appName, String ip, Integer post) throws Exception {

        List<GatewayFlowRuleEntity> gatewayFlowRuleEntityList = Lists.newArrayList();
        // 到nacos中拉取的配置
        List<GatewayFlowRule> gatewayFlowRuleList = getRules(appName);

        for (GatewayFlowRule gatewayFlowRule : gatewayFlowRuleList) {
            gatewayFlowRuleEntityList.add(GatewayFlowRuleEntity.fromGatewayFlowRule(appName, ip, post, gatewayFlowRule));
        }
        return gatewayFlowRuleEntityList;

    }
}
