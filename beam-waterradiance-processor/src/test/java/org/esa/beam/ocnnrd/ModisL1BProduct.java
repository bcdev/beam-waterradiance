package org.esa.beam.ocnnrd;


import org.esa.beam.framework.datamodel.Product;
import org.esa.beam.framework.datamodel.TiePointGrid;

class ModisL1BProduct {

    static Product create() {
        final Product modisL1BProduct = new Product("Modis L1B", "MODIS Level 1B", 2, 2);

        addSolarZenith(modisL1BProduct);
        addSolarAzimuth(modisL1BProduct);
        addSensorZenith(modisL1BProduct);

        return modisL1BProduct;
    }

    private static void addSolarZenith(Product modisL1BProduct) {
        float[] tiePointData = new float[4];
        tiePointData[0] = 50.231f;
        tiePointData[1] = 50.050995f;
        tiePointData[2] = 50.379997f;
        tiePointData[3] = 53.290997f;
        modisL1BProduct.addTiePointGrid(new TiePointGrid("SolarZenith", 2, 2, 0, 0, 1, 1, tiePointData));
    }

    private static void addSolarAzimuth(Product modisL1BProduct) {
        float[] tiePointData = new float[4];
        tiePointData[0] = -152.95699f;
        tiePointData[1] = -152.688f;
        tiePointData[2] = -150.57701f;
        tiePointData[3] = -150.9715f;
        modisL1BProduct.addTiePointGrid(new TiePointGrid("SolarAzimuth", 2, 2, 0, 0, 1, 1, tiePointData));
    }

    private static void addSensorZenith(Product modisL1BProduct) {
        float[] tiePointData = new float[4];
        tiePointData[0] = 12.4345f;
        tiePointData[1] = 11.712f;
        tiePointData[2] = 1.6778986f;
        tiePointData[3] = 12.0634985f;
        modisL1BProduct.addTiePointGrid(new TiePointGrid("SensorZenith", 2, 2, 0, 0, 1, 1, tiePointData));
    }
}

/*
Product:	A2006302181500.L1B_LAC

Image-X:	537	pixel
Image-Y:	800	pixel
Longitude:	77°30'47" W	degree
Latitude:	32°25'16" N	degree

BandName	Wavelength	Unit	Bandwidth	Unit	Value	Unit	Solar Flux	Unit
EV_1KM_RefSB_8:	412.0	nm	0.0	nm	0.10293106	none
EV_1KM_RefSB_9:	443.0	nm	0.0	nm	0.08083943	none
EV_1KM_RefSB_10:	488.0	nm	0.0	nm	0.057613704	none
EV_1KM_RefSB_11:	531.0	nm	0.0	nm	0.03888675	none
EV_1KM_RefSB_12:	547.0	nm	0.0	nm	0.033735927	none
EV_1KM_RefSB_13lo:	667.0	nm	0.0	nm	0.015850909	none
EV_1KM_RefSB_13hi:	667.0	nm	0.0	nm	0.015041743	none
EV_1KM_RefSB_14lo:	678.0	nm	0.0	nm	0.015179039	none
EV_1KM_RefSB_14hi:	678.0	nm	0.0	nm	0.014178071	none
EV_1KM_RefSB_15:	748.0	nm	0.0	nm	0.011262999	none
EV_1KM_RefSB_16:	869.0	nm	0.0	nm	0.0071801194	none
EV_1KM_RefSB_17:	905.0	nm	0.0	nm	1.3866447	none
EV_1KM_RefSB_18:	936.0	nm	0.0	nm	2.2100446	none
EV_1KM_RefSB_19:	940.0	nm	0.0	nm	1.6251919	none
EV_1KM_RefSB_26:	1375.0	nm	0.0	nm	0.015246164	none
EV_1KM_Emissive_20:	3750.0	nm	0.0	nm	0.7899856	none
EV_1KM_Emissive_21:	3959.0	nm	0.0	nm	235.8	none
EV_1KM_Emissive_22:	3959.0	nm	0.0	nm	1.0031369	none
EV_1KM_Emissive_23:	4050.0	nm	0.0	nm	1.082439	none
EV_1KM_Emissive_24:	4465.0	nm	0.0	nm	2.102	none
EV_1KM_Emissive_25:	4515.0	nm	0.0	nm	0.6641077	none
EV_1KM_Emissive_27:	6715.0	nm	0.0	nm	2.589501	none
EV_1KM_Emissive_28:	7325.0	nm	0.0	nm	4.4168153	none
EV_1KM_Emissive_29:	8550.0	nm	0.0	nm	11.553391	none
EV_1KM_Emissive_30:	9730.0	nm	0.0	nm	27.262001	none
EV_1KM_Emissive_31:	11030.0	nm	0.0	nm	11.847251	none
EV_1KM_Emissive_32:	12020.0	nm	0.0	nm	10.923304	none
EV_1KM_Emissive_33:	13335.0	nm	0.0	nm	17.848	none
EV_1KM_Emissive_34:	13635.0	nm	0.0	nm	13.666	none
EV_1KM_Emissive_35:	13935.0	nm	0.0	nm	12.026	none
EV_1KM_Emissive_36:	14235.0	nm	0.0	nm	8.068	none
EV_250_Aggr1km_RefSB_1:	645.0	nm	0.0	nm	0.08264944	none
EV_250_Aggr1km_RefSB_2:	859.0	nm	0.0	nm	0.10156423	none
EV_500_Aggr1km_RefSB_3:	469.0	nm	0.0	nm	0.23154846	none
EV_500_Aggr1km_RefSB_4:	555.0	nm	0.0	nm	0.22076042	none
EV_500_Aggr1km_RefSB_5:	1240.0	nm	0.0	nm	0.0	none
EV_500_Aggr1km_RefSB_6:	1640.0	nm	0.0	nm	0.0	none
EV_500_Aggr1km_RefSB_7:	2130.0	nm	0.0	nm	0.0	none

Height:	0.0
SensorAzimuth:	81.05548
Range:	721835.0
latitude:	32.42113
longitude:	-77.512955


Product:	A2006302181500.L1B_LAC

Image-X:	545	pixel
Image-Y:	831	pixel
Longitude:	77°20'52" W	degree
Latitude:	32°09'38" N	degree

BandName	Wavelength	Unit	Bandwidth	Unit	Value	Unit	Solar Flux	Unit
EV_1KM_RefSB_8:	412.0	nm	0.0	nm	0.10484232	none
EV_1KM_RefSB_9:	443.0	nm	0.0	nm	0.08215659	none
EV_1KM_RefSB_10:	488.0	nm	0.0	nm	0.058612272	none
EV_1KM_RefSB_11:	531.0	nm	0.0	nm	0.03944589	none
EV_1KM_RefSB_12:	547.0	nm	0.0	nm	0.034214936	none
EV_1KM_RefSB_13lo:	667.0	nm	0.0	nm	0.016265588	none
EV_1KM_RefSB_13hi:	667.0	nm	0.0	nm	0.015474172	none
EV_1KM_RefSB_14lo:	678.0	nm	0.0	nm	0.01560035	none
EV_1KM_RefSB_14hi:	678.0	nm	0.0	nm	0.014598195	none
EV_1KM_RefSB_15:	748.0	nm	0.0	nm	0.011674728	none
EV_1KM_RefSB_16:	869.0	nm	0.0	nm	0.007587253	none
EV_1KM_RefSB_17:	905.0	nm	0.0	nm	1.3866447	none
EV_1KM_RefSB_18:	936.0	nm	0.0	nm	2.2100446	none
EV_1KM_RefSB_19:	940.0	nm	0.0	nm	1.6251919	none
EV_1KM_RefSB_26:	1375.0	nm	0.0	nm	0.015435565	none
EV_1KM_Emissive_20:	3750.0	nm	0.0	nm	0.78790635	none
EV_1KM_Emissive_21:	3959.0	nm	0.0	nm	235.8	none
EV_1KM_Emissive_22:	3959.0	nm	0.0	nm	1.0041283	none
EV_1KM_Emissive_23:	4050.0	nm	0.0	nm	1.082259	none
EV_1KM_Emissive_24:	4465.0	nm	0.0	nm	2.102	none
EV_1KM_Emissive_25:	4515.0	nm	0.0	nm	0.67104465	none
EV_1KM_Emissive_27:	6715.0	nm	0.0	nm	2.6103573	none
EV_1KM_Emissive_28:	7325.0	nm	0.0	nm	4.467622	none
EV_1KM_Emissive_29:	8550.0	nm	0.0	nm	11.53278	none
EV_1KM_Emissive_30:	9730.0	nm	0.0	nm	27.262001	none
EV_1KM_Emissive_31:	11030.0	nm	0.0	nm	11.850505	none
EV_1KM_Emissive_32:	12020.0	nm	0.0	nm	10.939862	none
EV_1KM_Emissive_33:	13335.0	nm	0.0	nm	17.848	none
EV_1KM_Emissive_34:	13635.0	nm	0.0	nm	13.666	none
EV_1KM_Emissive_35:	13935.0	nm	0.0	nm	12.026	none
EV_1KM_Emissive_36:	14235.0	nm	0.0	nm	8.068	none
EV_250_Aggr1km_RefSB_1:	645.0	nm	0.0	nm	0.07408358	none
EV_250_Aggr1km_RefSB_2:	859.0	nm	0.0	nm	0.12825127	none
EV_500_Aggr1km_RefSB_3:	469.0	nm	0.0	nm	0.36442035	none
EV_500_Aggr1km_RefSB_4:	555.0	nm	0.0	nm	0.34981853	none
EV_500_Aggr1km_RefSB_5:	1240.0	nm	0.0	nm	0.0	none
EV_500_Aggr1km_RefSB_6:	1640.0	nm	0.0	nm	0.0	none
EV_500_Aggr1km_RefSB_7:	2130.0	nm	0.0	nm	0.0	none

Height:	0.0
SensorAzimuth:	80.81529
Range:	720038.25
latitude:	32.16066
longitude:	-77.34765


Product:	A2006302181500.L1B_LAC

Image-X:	693	pixel
Image-Y:	903	pixel
Longitude:	75°36'31" W	degree
Latitude:	31°45'14" N	degree

BandName	Wavelength	Unit	Bandwidth	Unit	Value	Unit	Solar Flux	Unit
EV_1KM_RefSB_8:	412.0	nm	0.0	nm	0.11404275	none
EV_1KM_RefSB_9:	443.0	nm	0.0	nm	0.09006805	none
EV_1KM_RefSB_10:	488.0	nm	0.0	nm	0.0634268	none
EV_1KM_RefSB_11:	531.0	nm	0.0	nm	0.04179913	none
EV_1KM_RefSB_12:	547.0	nm	0.0	nm	0.036203213	none
EV_1KM_RefSB_13lo:	667.0	nm	0.0	nm	0.01726501	none
EV_1KM_RefSB_13hi:	667.0	nm	0.0	nm	0.016451348	none
EV_1KM_RefSB_14lo:	678.0	nm	0.0	nm	0.016500099	none
EV_1KM_RefSB_14hi:	678.0	nm	0.0	nm	0.015507457	none
EV_1KM_RefSB_15:	748.0	nm	0.0	nm	0.0123815285	none
EV_1KM_RefSB_16:	869.0	nm	0.0	nm	0.008134709	none
EV_1KM_RefSB_17:	905.0	nm	0.0	nm	1.3866447	none
EV_1KM_RefSB_18:	936.0	nm	0.0	nm	2.2100446	none
EV_1KM_RefSB_19:	940.0	nm	0.0	nm	1.6251919	none
EV_1KM_RefSB_26:	1375.0	nm	0.0	nm	0.015648643	none
EV_1KM_Emissive_20:	3750.0	nm	0.0	nm	0.75470805	none
EV_1KM_Emissive_21:	3959.0	nm	0.0	nm	235.8	none
EV_1KM_Emissive_22:	3959.0	nm	0.0	nm	0.95577586	none
EV_1KM_Emissive_23:	4050.0	nm	0.0	nm	1.0393732	none
EV_1KM_Emissive_24:	4465.0	nm	0.0	nm	2.102	none
EV_1KM_Emissive_25:	4515.0	nm	0.0	nm	0.6683375	none
EV_1KM_Emissive_27:	6715.0	nm	0.0	nm	2.8926826	none
EV_1KM_Emissive_28:	7325.0	nm	0.0	nm	4.7855487	none
EV_1KM_Emissive_29:	8550.0	nm	0.0	nm	11.293233	none
EV_1KM_Emissive_30:	9730.0	nm	0.0	nm	27.262001	none
EV_1KM_Emissive_31:	11030.0	nm	0.0	nm	11.590834	none
EV_1KM_Emissive_32:	12020.0	nm	0.0	nm	10.732589	none
EV_1KM_Emissive_33:	13335.0	nm	0.0	nm	17.848	none
EV_1KM_Emissive_34:	13635.0	nm	0.0	nm	13.666	none
EV_1KM_Emissive_35:	13935.0	nm	0.0	nm	12.026	none
EV_1KM_Emissive_36:	14235.0	nm	0.0	nm	8.068	none
EV_250_Aggr1km_RefSB_1:	645.0	nm	0.0	nm	0.07282824	none
EV_250_Aggr1km_RefSB_2:	859.0	nm	0.0	nm	0.10756255	none
EV_500_Aggr1km_RefSB_3:	469.0	nm	0.0	nm	0.21511021	none
EV_500_Aggr1km_RefSB_4:	555.0	nm	0.0	nm	0.14029494	none
EV_500_Aggr1km_RefSB_5:	1240.0	nm	0.0	nm	0.0	none
EV_500_Aggr1km_RefSB_6:	1640.0	nm	0.0	nm	0.0	none
EV_500_Aggr1km_RefSB_7:	2130.0	nm	0.0	nm	0.0	none

Height:	0.0
SensorAzimuth:	-102.08755
Range:	706732.25
latitude:	31.753801
longitude:	-75.60853


Product:	A2006302181500.L1B_LAC

Image-X:	808	pixel
Image-Y:	582	pixel
Longitude:	75°07'23" W	degree
Latitude:	34°46'25" N	degree

BandName	Wavelength	Unit	Bandwidth	Unit	Value	Unit	Solar Flux	Unit
EV_1KM_RefSB_8:	412.0	nm	0.0	nm	0.11209194	none
EV_1KM_RefSB_9:	443.0	nm	0.0	nm	0.08847896	none
EV_1KM_RefSB_10:	488.0	nm	0.0	nm	0.06347435	none
EV_1KM_RefSB_11:	531.0	nm	0.0	nm	0.04329179	none
EV_1KM_RefSB_12:	547.0	nm	0.0	nm	0.0375452	none
EV_1KM_RefSB_13lo:	667.0	nm	0.0	nm	0.018259773	none
EV_1KM_RefSB_13hi:	667.0	nm	0.0	nm	0.01746222	none
EV_1KM_RefSB_14lo:	678.0	nm	0.0	nm	0.017499821	none
EV_1KM_RefSB_14hi:	678.0	nm	0.0	nm	0.01649781	none
EV_1KM_RefSB_15:	748.0	nm	0.0	nm	0.013095192	none
EV_1KM_RefSB_16:	869.0	nm	0.0	nm	0.008551724	none
EV_1KM_RefSB_17:	905.0	nm	0.0	nm	1.3866447	none
EV_1KM_RefSB_18:	936.0	nm	0.0	nm	2.2100446	none
EV_1KM_RefSB_19:	940.0	nm	0.0	nm	1.6251919	none
EV_1KM_RefSB_26:	1375.0	nm	0.0	nm	0.01541189	none
EV_1KM_Emissive_20:	3750.0	nm	0.0	nm	0.788946	none
EV_1KM_Emissive_21:	3959.0	nm	0.0	nm	235.8	none
EV_1KM_Emissive_22:	3959.0	nm	0.0	nm	1.0038996	none
EV_1KM_Emissive_23:	4050.0	nm	0.0	nm	1.0771344	none
EV_1KM_Emissive_24:	4465.0	nm	0.0	nm	2.102	none
EV_1KM_Emissive_25:	4515.0	nm	0.0	nm	0.6544636	none
EV_1KM_Emissive_27:	6715.0	nm	0.0	nm	2.5352983	none
EV_1KM_Emissive_28:	7325.0	nm	0.0	nm	4.365046	none
EV_1KM_Emissive_29:	8550.0	nm	0.0	nm	11.505482	none
EV_1KM_Emissive_30:	9730.0	nm	0.0	nm	27.262001	none
EV_1KM_Emissive_31:	11030.0	nm	0.0	nm	11.83814	none
EV_1KM_Emissive_32:	12020.0	nm	0.0	nm	10.9273	none
EV_1KM_Emissive_33:	13335.0	nm	0.0	nm	17.848	none
EV_1KM_Emissive_34:	13635.0	nm	0.0	nm	13.666	none
EV_1KM_Emissive_35:	13935.0	nm	0.0	nm	12.026	none
EV_1KM_Emissive_36:	14235.0	nm	0.0	nm	8.068	none
EV_250_Aggr1km_RefSB_1:	645.0	nm	0.0	nm	0.07430512	none
EV_250_Aggr1km_RefSB_2:	859.0	nm	0.0	nm	0.11887009	none
EV_500_Aggr1km_RefSB_3:	469.0	nm	0.0	nm	0.21124794	none
EV_500_Aggr1km_RefSB_4:	555.0	nm	0.0	nm	0.13815603	none
EV_500_Aggr1km_RefSB_5:	1240.0	nm	0.0	nm	0.0	none
EV_500_Aggr1km_RefSB_6:	1640.0	nm	0.0	nm	0.0	none
EV_500_Aggr1km_RefSB_7:	2130.0	nm	0.0	nm	0.0	none

Height:	0.0
SensorAzimuth:	-99.980995
Range:	721265.0
latitude:	34.77362
longitude:	-75.122925

 */
