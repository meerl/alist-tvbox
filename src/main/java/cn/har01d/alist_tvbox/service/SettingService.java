package cn.har01d.alist_tvbox.service;

import cn.har01d.alist_tvbox.config.AppProperties;
import cn.har01d.alist_tvbox.entity.Setting;
import cn.har01d.alist_tvbox.entity.SettingRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SettingService {
    private final JdbcTemplate jdbcTemplate;
    private final AppProperties appProperties;
    private final SettingRepository settingRepository;

    public SettingService(JdbcTemplate jdbcTemplate, AppProperties appProperties, SettingRepository settingRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.appProperties = appProperties;
        this.settingRepository = settingRepository;
    }

    @PostConstruct
    public void setup() {
        appProperties.setMerge(settingRepository.findById("merge_site_source").map(Setting::getValue).orElse("").equals("true"));
        appProperties.setMix(!settingRepository.findById("mix_site_source").map(Setting::getValue).orElse("").equals("false"));
    }

    public void exportDatabase() {
        jdbcTemplate.execute("SCRIPT TO '/data/data-h2.sql' TABLE ACCOUNT, ALIST_ALIAS, CONFIG_FILE, INDEX_TEMPLATE, PIK_PAK_ACCOUNT, SETTING, SHARE, SITE, SUBSCRIPTION, USERS");
    }

    public Map<String, String> findAll() {
        Map<String, String> map = settingRepository.findAll().stream().collect(Collectors.toMap(Setting::getName, Setting::getValue));
        map.remove("atv_password");
        return map;
    }

    public Setting update(Setting setting) {
        if ("merge_site_source".equals(setting.getName())) {
            appProperties.setMerge("true".equals(setting.getValue()));
        }
        if ("mix_site_source".equals(setting.getName())) {
            appProperties.setMix("true".equals(setting.getValue()));
        }
        return settingRepository.save(setting);
    }

}
