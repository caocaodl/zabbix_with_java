
function completeLoading() {
    setTimeout(function() {
      Ext.get('loading').remove();
      Ext.get('loading-mask').fadeOut({ remove: true });
    }, 250);
}
