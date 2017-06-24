Ext.ux.OneCombo = Ext.extend(Ext.form.ComboBox, {
			initComponent : function() {
				this.readOnly = true;
				this.displayField = 'text';
				this.valueField = 'text';
				this.triggerAction = 'all';
				this.mode = 'local';
				this.emptyText = 'Please Select...';

				this.store = new Ext.data.SimpleStore({
							expandData : true,
							fields : ['text']
						});
				this.store.loadData(this.data);

				Ext.ux.OneCombo.superclass.initComponent.call(this);
			}
		});
Ext.reg('onecombo', Ext.ux.OneCombo);

Ext.ux.TwoCombo = Ext.extend(Ext.form.ComboBox, {
	initComponent : function() {
		this.readOnly = true;
		this.displayField = 'text';
		this.valueField = 'value';
		this.triggerAction = 'all';
		this.mode = 'local';
		this.emptyText = 'Please Select...';

		this.store = new Ext.data.SimpleStore({
					fields : ['value', 'text']
				});
		this.store.loadData(this.data);

		Ext.ux.TwoCombo.superclass.initComponent.call(this);
	}
});

Ext.reg('twocombo', Ext.ux.TwoCombo);