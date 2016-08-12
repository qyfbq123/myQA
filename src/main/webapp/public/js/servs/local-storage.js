// Generated by CoffeeScript 1.10.0
define(['_', 'lscache'], function(un, lscache) {
  return {
    set: function(key, value) {
      if (_.isObject(value)) {
        value = JSON.stringify(value);
      }
      return lscache.set(key, value, 120);
    },
    get: function(key) {
      var data, e, error;
      data = lscache.get(key);
      try {
        data = JSON.parse(data);
      } catch (error) {
        e = error;
      }
      return data;
    },
    remove: function(key) {
      return lscache.remove(key);
    },
    "delete": function(key) {
      return lscache.remove(key);
    },
    rm: function(key) {
      return lscache.remove(key);
    }
  };
});
